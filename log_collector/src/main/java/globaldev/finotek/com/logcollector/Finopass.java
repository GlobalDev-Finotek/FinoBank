package globaldev.finotek.com.logcollector;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import globaldev.finotek.com.logcollector.api.ApiModule;
import globaldev.finotek.com.logcollector.api.log.ApiServiceImpl;
import globaldev.finotek.com.logcollector.api.user.UserInitResponse;
import globaldev.finotek.com.logcollector.api.user.UserServiceImpl;
import globaldev.finotek.com.logcollector.log.AppUsageLoggingService;
import globaldev.finotek.com.logcollector.log.BaseLoggingService;
import globaldev.finotek.com.logcollector.log.CallLogHistoryLoggingService;
import globaldev.finotek.com.logcollector.log.LocationLogService;
import globaldev.finotek.com.logcollector.log.LoggingHelper;
import globaldev.finotek.com.logcollector.log.LoggingHelperImpl;
import globaldev.finotek.com.logcollector.log.SMSLoggingService;
import globaldev.finotek.com.logcollector.model.ActionConfig;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.model.User;
import globaldev.finotek.com.logcollector.model.UserDevice;
import globaldev.finotek.com.logcollector.model.ValueQueryGenerator;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoGetter;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoGetterImpl;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.ReplaySubject;
import io.realm.Realm;


/**
 * Created by magyeong-ug on 13/06/2017.
 */

public class Finopass {

	private static final int PERMISSION_READ_LOG = 11234;

	private static Finopass instance;
	private UserInfoGetter userInfoGetter;
	private UserServiceImpl userService;
	private SharedPreferences sharedPrefs;
	private LoggingHelper loggingHelper;
	private ReplaySubject<Boolean> registerSubject;
	private Activity activity;
	private ApiServiceImpl apiService;

	public static Finopass getInstance(Activity activity) {
		if (instance == null) {
			instance = new Finopass(activity);
			instance.activity = activity;
		}

		return instance;
	}

	private Finopass(Activity activity) {
		init(activity);
	}


	private void initModules(Application app) {
		sharedPrefs = app.getSharedPreferences("prefs", Context.MODE_PRIVATE);

		userInfoGetter = new UserInfoGetterImpl(app, sharedPrefs);
		userService = new ApiModule().getUserService(app);
		apiService = new ApiModule().getApiService(app);

		loggingHelper = new LoggingHelperImpl(app.getApplicationContext());

	}

	private void runLoggingService() {
		Map<String, String> serviceMap = getServiceMap();
		loggingHelper.runLoggingService(serviceMap);
	}

	private ActionConfig getActionConfig(int actionType, int period) {
		ActionConfig actionConfig = new ActionConfig(actionType);
		actionConfig.setPeriod(period);
		actionConfig.setPersisted(true);
		actionConfig.setRequiresCharging(true);
		actionConfig.setRequiresDeviceIdle(false);
		actionConfig.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
		return actionConfig;
	}

	private String convertServiceMapToJson(ActionConfig actionConfig) {
		return new Gson().toJson(actionConfig);
	}

	private Map getServiceMap() {
		HashMap<String, String> serviceMap = new HashMap<>();

		serviceMap.put("phoneCall",
				convertServiceMapToJson(getActionConfig(
						ActionType.GATHER_CALL_LOG,
						1000 * 60 * 60 * 24)));

		serviceMap.put("location",
				convertServiceMapToJson(getActionConfig(
						ActionType.GATHER_LOCATION_LOG,
						1000 * 60 * 10)));

		serviceMap.put("message",
				convertServiceMapToJson(getActionConfig(
						ActionType.GATHER_MESSAGE_LOG,
						1000 * 60 * 60 * 24)));

		serviceMap.put("application",
				convertServiceMapToJson(getActionConfig(
						ActionType.GATHER_APP_USAGE_LOG,
						1000 * 60 * 60 * 24)));

		return serviceMap;
	}

	private void init(Activity activity) {
		registerSubject = ReplaySubject.create();

		Application app = activity.getApplication();
		Realm.init(app);

		initModules(app);

   /*
    앱 사용기록 사용권환 얻기 -> 나타나는 설정 화면에서 앱 사용기록 접근 허용으로 체크해야합니다.
    */
		if (!hasPermission(activity)) {
			Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
			activity.startActivity(intent);
		}

		String userKey = userInfoGetter.getUserKey();

		if(TextUtils.isEmpty(userKey)) {

			ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_CALL_LOG,
							android.Manifest.permission.READ_SMS, android.Manifest.permission.SEND_SMS,
							android.Manifest.permission.BROADCAST_SMS, android.Manifest.permission.RECEIVE_SMS,
							Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
							Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission_group.SMS,
							android.Manifest.permission.READ_PHONE_STATE},
					PERMISSION_READ_LOG);

		} else {
			registerSubject.onNext(true);
			registerSubject.onComplete();
		}

	}

	private boolean hasPermission(Activity activity) {
		AppOpsManager appOps = (AppOpsManager)
				activity.getSystemService(Context.APP_OPS_SERVICE);
		int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
				android.os.Process.myUid(), activity.getPackageName());
		return mode == AppOpsManager.MODE_ALLOWED;
	}

	public Flowable getScore(List<ValueQueryGenerator> valueQueryGenerators) {
		return apiService.getRecentLogs(userInfoGetter.getUserKey(), valueQueryGenerators);
	}


	public void onAllowedPermission(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

		if (requestCode == PERMISSION_READ_LOG) {
			final User user = new User(userInfoGetter.getPhoneNumber(), userInfoGetter.getUserName(), userInfoGetter.getEmail());

			UserDevice device = new UserDevice(userInfoGetter.getDeviceId(),
					userInfoGetter.getDeviceType(), userInfoGetter.getDeviceModel(), " ");

			userService.init(user, device)
					.subscribe(new Consumer<UserInitResponse>() {
						@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
						@Override
						public void accept(UserInitResponse userInitResponse) throws Exception {

							String userKey = userInitResponse.getToken();

							sharedPrefs
									.edit()
									.putString(activity.getString(R.string.user_key), userKey)
									.apply();

						}
					}, new Consumer<Throwable>() {
						@Override
						public void accept(Throwable throwable) throws Exception {
							System.out.println(throwable.getMessage());
							registerSubject.onNext(false);
							registerSubject.onComplete();
						}
					}, new Action() {
						@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
						@Override
						public void run() throws Exception {
							uploadAllLogs();
							runLoggingService();
							registerSubject.onNext(true);
							registerSubject.onComplete();
						}
					});
		}

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

			final JobScheduler jobService = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);

			PersistableBundle b = new PersistableBundle();
			b.putBoolean("isGetAllData", true);

			JobInfo job = new JobInfo.Builder(loggingService.JOB_ID + 1, new ComponentName(activity, loggingService.getClass()))
					.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
					.setRequiresDeviceIdle(true)
					.setRequiresCharging(true)
					.setExtras(b)
					.build();

			jobService.schedule(job);


		}


	}


	public Observable<Boolean> registerObservable() {
		return registerSubject;
	}
}
