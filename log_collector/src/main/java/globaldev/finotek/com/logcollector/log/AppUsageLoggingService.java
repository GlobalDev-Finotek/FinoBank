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
import globaldev.finotek.com.logcollector.app.FinopassApp;
import globaldev.finotek.com.logcollector.model.ApplicationLog;
import globaldev.finotek.com.logcollector.util.AesInstance;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;

public class AppUsageLoggingService extends BaseLoggingService<ApplicationLog> {

	@Inject
	Context context;

	@Inject
	RxEventBus eventBus;

	@Inject
	SharedPreferences sharedPreferences;

	AesInstance ai;

	public AppUsageLoggingService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		((FinopassApp) getApplication()).getAppComponent().inject(this);

		String key = sharedPreferences.getString(
				context.getString(R.string.shared_prefs_push_token), "")
				.substring(0, 16);

		try {
			ai = AesInstance.getInstance(key.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void parse() {
		List<UsageStats> usageStatsList = this.getUsageStatsList(getBaseContext());

		for (UsageStats u : usageStatsList) {
			PackageManager pm = context.getPackageManager();
			try {
				PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(u.getPackageName(), 0);
				String label = ai.encText(String.valueOf(foregroundAppPackageInfo.applicationInfo.loadLabel(pm)));

				ApplicationLog log = new ApplicationLog(label,
						String.valueOf(u.getTotalTimeInForeground()),
						u.getLastTimeUsed());

				logData.add(log);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void notifyJobDone(List<ApplicationLog> logData) {
		eventBus.publish(RxEventBus.PARSING_APP_USAGE_FINISHED, logData);
	}


	private List<UsageStats> getUsageStatsList(Context context) {
		UsageStatsManager usm =
				(UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		long endTime = calendar.getTimeInMillis();

		calendar.add(Calendar.HOUR, -1);

		long startTime = calendar.getTimeInMillis();

		return usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
	}

}
