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

import globaldev.finotek.com.logcollector.R;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.model.ApplicationLog;
import globaldev.finotek.com.logcollector.util.AesInstance;

public class AppUsageLoggingService extends BaseLoggingService<ApplicationLog> {


	private SharedPreferences sharedPreferences;


	public AppUsageLoggingService() {
		JOB_ID = ActionType.GATHER_APP_USAGE_LOG;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
	}

	@Override
	protected Class getDBClass() {
		return ApplicationLog.class;
	}

	@Override
	public void getData(boolean isGetAllData) {

		String key = sharedPreferences.getString(
				getBaseContext().getString(R.string.user_key), "")
				.substring(0, 16);

		AesInstance ai = null;
		try {
			ai = AesInstance.getInstance(key.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		UsageStatsManager usm =
				(UsageStatsManager) getBaseContext().getSystemService(Context.USAGE_STATS_SERVICE);

		long startTime;

		List<UsageStats> usageStatsList;

		if (isGetAllData) {

			Calendar beginCal = Calendar.getInstance();
			beginCal.add(Calendar.YEAR, -2);

			usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY,
					beginCal.getTimeInMillis(),
					System.currentTimeMillis());

		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, -1);

			startTime = calendar.getTimeInMillis();

			usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
					startTime, System.currentTimeMillis());
		}


		for (UsageStats u : usageStatsList) {
			PackageManager pm = getBaseContext().getPackageManager();
			try {
				PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(u.getPackageName(), 0);
				String label = String.valueOf(foregroundAppPackageInfo.applicationInfo.loadLabel(pm));
				String packageName = foregroundAppPackageInfo.applicationInfo.packageName;

				if (ai != null)
					label = ai.encText(label);

				long duration = u.getTotalTimeInForeground();

				if (duration > 0) {
					ApplicationLog log = new ApplicationLog(label,
							String.valueOf(u.getFirstTimeStamp()),
							duration);
					log.lastTimeUsed = String.valueOf(u.getLastTimeStamp());
					log.packageName = packageName;

					logData.add(log);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
