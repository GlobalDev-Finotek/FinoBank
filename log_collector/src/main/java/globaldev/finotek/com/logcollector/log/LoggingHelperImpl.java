package globaldev.finotek.com.logcollector.log;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.api.log.ApiServiceImpl;
import globaldev.finotek.com.logcollector.app.FinopassApp;
import globaldev.finotek.com.logcollector.model.ActionConfig;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;

/**
 * Created by magyeong-ug on 26/04/2017.
 */

public class LoggingHelperImpl implements LoggingHelper {

	private final JobScheduler mJobService;
	@Inject
	Context context;

	@Inject
	SharedPreferences sharedPreferences;

	@Inject
	ApiServiceImpl logService;

	@Inject
	RxEventBus eventBus;

	private HashMap<Integer, String> loggingServiceMap;

	@Inject
	public LoggingHelperImpl(Context context) {
		this.context = context;
		((FinopassApp) context).getAppComponent().inject(this);
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


	private List<JobInfo> parseJobInfo(Map<String, String> dataMap) throws NumberFormatException, UnregisteredServiceException {

		ArrayList<JobInfo> jobInfos = new ArrayList<>();
		ArrayList<String> keySet = new ArrayList<>(dataMap.keySet());

		for (String key : keySet) {
			String json = dataMap.get(key);

			try {
				ActionConfig actionConfig = new Gson().fromJson(json, ActionConfig.class);
				int actionType = actionConfig.getActionType();

				if (actionType > 0) {
					registerService(actionType, json);

					JobInfo.Builder jobBuilder = new JobInfo.Builder(actionType, new ComponentName(context, loggingServiceMap.get(actionType)))
							.setRequiredNetworkType(actionConfig.getRequiredNetworkType())
							.setRequiresCharging(actionConfig.isRequiresCharging())
							.setRequiresDeviceIdle(actionConfig.isRequiresDeviceIdle())
							.setPersisted(actionConfig.isPersisted());

					if (actionType == ActionType.GATHER_LOCATION_LOG) {
						jobBuilder.setPeriodic(actionConfig.getPeriod());
					}

					jobInfos.add(jobBuilder.build());
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}


		return jobInfos;
	}




	private void registerService(int actionType, String json) {
		switch (actionType) {
			case ActionType.GATHER_CALL_LOG:
				ActionConfig<ActionConfig.CallHistoryLogOption> actionConfig =
						new Gson().fromJson(json, getType(ActionConfig.class, ActionConfig.CallHistoryLogOption.class));
				loggingServiceMap.put(ActionType.GATHER_CALL_LOG, CallLogHistoryLoggingService.class.getName());
				break;

			case ActionType.GATHER_APP_USAGE_LOG:
				loggingServiceMap.put(ActionType.GATHER_APP_USAGE_LOG,
						AppUsageLoggingService.class.getName());
				break;

			case ActionType.GATHER_LOCATION_LOG:
				loggingServiceMap.put(ActionType.GATHER_LOCATION_LOG,
						LocationLogService.class.getName());
				break;

			case ActionType.GATHER_MESSAGE_LOG:
				loggingServiceMap.put(ActionType.GATHER_MESSAGE_LOG,
						SMSLoggingService.class.getName());
				break;

			default:
//				return new Gson().fromJson(json, ActionConfig.class);
		}

	}


	private Type getType(final Class<?> rawClass, final Class<?> parameter) {
		return new ParameterizedType() {
			@Override
			public Type[] getActualTypeArguments() {
				return new Type[]{parameter};
			}

			@Override
			public Type getRawType() {
				return rawClass;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}
		};
	}

}
