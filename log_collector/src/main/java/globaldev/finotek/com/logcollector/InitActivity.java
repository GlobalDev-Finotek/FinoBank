package globaldev.finotek.com.logcollector;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.api.log.ApiServiceImpl;
import globaldev.finotek.com.logcollector.api.user.UserInitResponse;
import globaldev.finotek.com.logcollector.api.user.UserServiceImpl;
import globaldev.finotek.com.logcollector.app.FinopassApp;
import globaldev.finotek.com.logcollector.log.AppUsageLoggingService;
import globaldev.finotek.com.logcollector.log.BaseLoggingService;
import globaldev.finotek.com.logcollector.log.CallLogHistoryLoggingService;
import globaldev.finotek.com.logcollector.log.LocationLogService;
import globaldev.finotek.com.logcollector.log.SMSLoggingService;
import globaldev.finotek.com.logcollector.model.User;
import globaldev.finotek.com.logcollector.model.UserDevice;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoService;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by magyeong-ug on 10/05/2017.
 */

public abstract class InitActivity extends AppCompatActivity {

	public static final int PERMISSION_READ_LOG = 11234;

	@Inject
	RxEventBus eventBus;

	@Inject
	ApiServiceImpl logService;

	@Inject
	UserServiceImpl userService;

	@Inject
	UserInfoService userInfoGetter;

	@Inject
	SharedPreferences sharedPreferences;

	void init() {
		((FinopassApp) getApplication()).getAppComponent().inject(this);

		ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CALL_LOG,
						android.Manifest.permission.READ_SMS, android.Manifest.permission.SEND_SMS,
						android.Manifest.permission.BROADCAST_SMS, android.Manifest.permission.RECEIVE_SMS,
						Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.GET_ACCOUNTS, Manifest.permission.ACCESS_WIFI_STATE,
						android.Manifest.permission_group.SMS, android.Manifest.permission.READ_PHONE_STATE},
				PERMISSION_READ_LOG);

    /*
    앱 사용기록 사용권환 얻기 -> 나타나는 설정 화면에서 앱 사용기록 접근 허용으로 체크해야합니다.
    */
		if (!hasPermission()) {
			Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
			startActivity(intent);
		} else {
			onAfterUserRegistered();


		}

	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
		init();
	}

	public boolean hasPermission() {
		AppOpsManager appOps = (AppOpsManager)
				getSystemService(Context.APP_OPS_SERVICE);
		int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
				android.os.Process.myUid(), getPackageName());
		return mode == AppOpsManager.MODE_ALLOWED;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		eventBus.unregister(this);
	}

	protected abstract void onAfterUserRegistered();

	protected abstract void onAfterGetPermission();

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		onAfterGetPermission();
	}

	private List<BaseLoggingService> getAllLoggingServices() {
		ArrayList<BaseLoggingService> baseLoggingServices = new ArrayList<>();

		baseLoggingServices.add(new CallLogHistoryLoggingService());
		baseLoggingServices.add(new SMSLoggingService());
		baseLoggingServices.add(new AppUsageLoggingService());
		baseLoggingServices.add(new LocationLogService());


		return baseLoggingServices;
	}


	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
	private void uploadAllLogs() {
		List<BaseLoggingService> allLoggingServices = getAllLoggingServices();

		for (BaseLoggingService loggingService : allLoggingServices) {

			final JobScheduler jobService = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

			PersistableBundle b = new PersistableBundle();
			b.putBoolean("isGetAllData", true);

			JobInfo job = new JobInfo.Builder(loggingService.JOB_ID, new ComponentName(this, loggingService.getClass()))
					.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
					.setRequiresDeviceIdle(false)
					.setRequiresCharging(false)
					.setExtras(b)
					.build();

			jobService.schedule(job);


		}


	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == PERMISSION_READ_LOG) {
			eventBus.subscribe(RxEventBus.REGISTER_FCM, this, new Consumer<Object>() {
				@Override
				public void accept(Object o) throws Exception {
					String pushToken = (String) o;

					final User user = new User(userInfoGetter.getPhoneNumber(), userInfoGetter.getUserName(), userInfoGetter.getEmail(), "gender");

					UserDevice device = new UserDevice(userInfoGetter.getDeviceId(),
							userInfoGetter.getDeviceType(), userInfoGetter.getDeviceModel(), true, pushToken);

					userService.init(user, device)
							.subscribe(new Consumer<UserInitResponse>() {
								@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
								@Override
								public void accept(UserInitResponse userInitResponse) throws Exception {

									String userKey = userInitResponse.getToken();
									sharedPreferences
											.edit()
											.putString(getString(R.string.user_key), userKey)
											.apply();

								}
							}, new Consumer<Throwable>() {
								@Override
								public void accept(Throwable throwable) throws Exception {
									System.out.println(throwable.getMessage());
								}
							}, new Action() {
								@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
								@Override
								public void run() throws Exception {
									// uploadAllLogs();
									onAfterUserRegistered();
									eventBus.unregister(InitActivity.this);
								}
							});
				}
			});
		}
	}
}
