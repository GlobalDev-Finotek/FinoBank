package globaldev.finotek.com.logcollector.log;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.R;
import globaldev.finotek.com.logcollector.api.log.ApiServiceImpl;
import globaldev.finotek.com.logcollector.app.FinopassApp;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.model.ApplicationLog;
import globaldev.finotek.com.logcollector.util.AesInstance;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;

public class AppUsageLoggingService extends BaseLoggingService<ApplicationLog> {

	@Inject
	RxEventBus eventBus;

	@Inject
	SharedPreferences sharedPreferences;

	@Inject
	ApiServiceImpl logService;

	AesInstance ai;

	public AppUsageLoggingService() {
		JOB_ID = ActionType.GATHER_APP_USAGE_LOG;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		((FinopassApp) getApplication()).getAppComponent().inject(this);

		String key = sharedPreferences.getString(
				getBaseContext().getString(R.string.user_key), "")
				.substring(0, 16);

		try {
			ai = AesInstance.getInstance(key.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected Class getDBClass() {
		return ApplicationLog.class;
	}

	@Override
	public void getData(boolean isGetAllData) {

		UsageStatsManager usm =
				(UsageStatsManager) getBaseContext().getSystemService(Context.USAGE_STATS_SERVICE);

		long startTime = 0, endTime = 0;

		if (isGetAllData) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			endTime = calendar.getTimeInMillis();

			calendar.add(Calendar.HOUR, -1);

			startTime = calendar.getTimeInMillis();
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			endTime = calendar.getTimeInMillis();

			calendar.add(Calendar.HOUR, -1);

			startTime = calendar.getTimeInMillis();
		}

		List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);

		for (UsageStats u : usageStatsList) {
			PackageManager pm = getBaseContext().getPackageManager();
			try {
				PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(u.getPackageName(), 0);
				String label = String.valueOf(foregroundAppPackageInfo.applicationInfo.loadLabel(pm));

				long duration = u.getTotalTimeInForeground();

				if (duration > 0) {
					ApplicationLog log = new ApplicationLog(label,
							String.valueOf(u.getFirstTimeStamp()),
							duration);

					logData.add(log);
				}

			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}


	/*
	@Override
	protected void uploadLogs(String userKey) {

		logService.updateAppUsageLog(userKey, logData)
				.retry(3)
				.subscribe(new Consumer() {
					@Override
					public void accept(Object o) throws Exception {
						System.out.print(o);
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						saveLogs();
						System.out.print(throwable);
					}
				}, new Action() {
					@Override
					public void run() throws Exception {
						clearDB(ApplicationLog.class);
					}
				});
	}
	*/


}
