package globaldev.finotek.com.logcollector;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.crash.FirebaseCrash;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.api.user.UserInitResponse;
import globaldev.finotek.com.logcollector.api.user.UserServiceImpl;
import globaldev.finotek.com.logcollector.app.FinopassApp;
import globaldev.finotek.com.logcollector.log.LoggingHelper;
import globaldev.finotek.com.logcollector.model.User;
import globaldev.finotek.com.logcollector.model.UserDevice;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoService;
import io.reactivex.functions.Consumer;

/**
 * Created by magyeong-ug on 10/05/2017.
 */

public abstract class InitActivity extends AppCompatActivity {

	private static final int MY_PERMISSION_READ_CALL_LOG = 11234;

	@Inject
	RxEventBus eventBus;

	@Inject
	LoggingHelper loggingHelper;

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
						android.Manifest.permission_group.SMS,
						android.Manifest.permission.READ_PHONE_STATE},
				MY_PERMISSION_READ_CALL_LOG);

       /*
            앱 사용기록 사용권환 얻기
            > 나타나는 설정 화면에서 앱 사용기록 접근 허용으로 체크해야합니다.
        */
		if (!hasPermission()) {
			Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
			startActivity(intent);
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

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == MY_PERMISSION_READ_CALL_LOG) {
			eventBus.subscribe(RxEventBus.REGISTER_FCM, this, new Consumer<Object>() {
				@Override
				public void accept(Object o) throws Exception {
					String pushToken = (String) o;

					User user = new User(userInfoGetter.getPhoneNumber(), " ", "gender");

					UserDevice device = new UserDevice(userInfoGetter.getDeviceId(),
							userInfoGetter.getDeviceType(), userInfoGetter.getDeviceModel(), true, pushToken);

					userService.init(user, device)
							.subscribe(new Consumer<UserInitResponse>() {
								@Override
								public void accept(UserInitResponse userInitResponse) throws Exception {
									sharedPreferences
											.edit()
											.putString(getString(R.string.shared_prefs_push_token), userInitResponse.getToken())
											.apply();

									onAfterUserRegistered();
								}
							}, new Consumer<Throwable>() {
								@Override
								public void accept(Throwable throwable) throws Exception {
									FirebaseCrash.report(throwable);
								}
							});
				}
			});

		}
	}
}
