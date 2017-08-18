package globaldev.finotek.com.logcollector.log;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import globaldev.finotek.com.logcollector.model.ActionConfig;

import static globaldev.finotek.com.logcollector.model.ActionType.GATHER_APP_USAGE_LOG;
import static globaldev.finotek.com.logcollector.model.ActionType.GATHER_CALL_LOG;
import static globaldev.finotek.com.logcollector.model.ActionType.GATHER_LOCATION_LOG;
import static globaldev.finotek.com.logcollector.model.ActionType.GATHER_MESSAGE_LOG;

/**
 * Created by magyeong-ug on 26/04/2017.
 */

public class LoggingHelperImpl implements LoggingHelper {

	private final JobScheduler mJobService;
	private Context context;
	private HashMap<Integer, String> loggingServiceMap;


	public LoggingHelperImpl(Context context) {
		this.context = context;
		loggingServiceMap = new HashMap<>();
		mJobService =
				(JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
	}

	@Override
	public void runLoggingService(Map<String, String> dataMap) {

		List<JobInfo> jobs = new ArrayList<>();
		try {
			jobs = parseJobInfo(dataMap);
		} catch (UnregisteredServiceException e) {
			e.printStackTrace();
		}

		for (JobInfo job : jobs) {
			try {
				if (isInvokeService(job)) {

					scheduleLoggingService(job);

				} else {
				  /* According to defined action type inversion action type with * -1 is
				  * a cancel operation of specific action.
			    * {@link globaldev.finotek.com.logcollector.model.ActionType }
			    */
					mJobService.cancel(job.getId() * -1);
				}
			} catch (NumberFormatException | NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	private void scheduleLoggingService(JobInfo job) {
		if (JobScheduler.RESULT_SUCCESS == mJobService.schedule(job)) {
			System.out.println("service success");
		}
	}

	private boolean isInvokeService(JobInfo job) {
		return job.getId() > 0;
	}


	private List<JobInfo> parseJobInfo(Map<String, String> dataMap) throws UnregisteredServiceException {

		ArrayList<JobInfo> jobInfos = new ArrayList<>();


		for (String key : dataMap.keySet()) {
			String json = dataMap.get(key);

			ActionConfig actionConfig = new Gson().fromJson(json, ActionConfig.class);
			int actionType = actionConfig.getActionType();

			if (actionType > 0) {
				registerService(actionType);

				String serviceName = loggingServiceMap.get(actionType);

				if (TextUtils.isEmpty(serviceName)) {
					throw new UnregisteredServiceException();
				}

				JobInfo.Builder jobBuilder = new JobInfo.Builder(actionType, new ComponentName(context, serviceName))
						.setRequiredNetworkType(actionConfig.getRequiredNetworkType())
						.setRequiresCharging(actionConfig.isRequiresCharging())
						.setRequiresDeviceIdle(actionConfig.isRequiresDeviceIdle())
						.setPersisted(actionConfig.isPersisted());

				if (actionType == GATHER_LOCATION_LOG) {
					jobBuilder.setPeriodic(actionConfig.getPeriod());
				}

				jobInfos.add(jobBuilder.build());
			}

		}


		return jobInfos;
	}


	private void registerService(int actionType) {
		switch (actionType) {
			case GATHER_CALL_LOG:
				loggingServiceMap.put(GATHER_CALL_LOG, CallLogHistoryLoggingService.class.getName());
				break;

			case GATHER_APP_USAGE_LOG:
				loggingServiceMap.put(GATHER_APP_USAGE_LOG,
						AppUsageLoggingService.class.getName());
				break;

			case GATHER_LOCATION_LOG:
				loggingServiceMap.put(GATHER_LOCATION_LOG,
						LocationLogService.class.getName());
				break;

			case GATHER_MESSAGE_LOG:
				loggingServiceMap.put(GATHER_MESSAGE_LOG,
						SMSLoggingService.class.getName());
				break;

			default:
//				return new Gson().fromJson(json, ActionConfig.class);
		}

	}


}
