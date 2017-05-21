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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.R;
import globaldev.finotek.com.logcollector.api.log.ApiServiceImpl;
import globaldev.finotek.com.logcollector.app.MyApplication;
import globaldev.finotek.com.logcollector.model.ActionConfig;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.model.ApplicationLog;
import globaldev.finotek.com.logcollector.model.CallHistoryLog;
import globaldev.finotek.com.logcollector.model.DeviceSecurityLevel;
import globaldev.finotek.com.logcollector.model.LocationLog;
import globaldev.finotek.com.logcollector.model.MessageLog;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.realm.Realm;

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
		((MyApplication) context).getAppComponent().inject(this);
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
			callUploadApi(job.getId());
		}
	}

	private boolean isInvokeService(JobInfo job) {
		return job.getId() > 0;
	}


	private List<JobInfo> parseJobInfo(Map<String, String> dataMap) throws NumberFormatException, UnregisteredServiceException {

		ArrayList<JobInfo> jobInfos = new ArrayList<>();
		ArrayList<String> keySet = new ArrayList<>(dataMap.keySet());

		/* UPLOAD LOG 가 마지막에 위치해야 함 */
		Collections.sort(keySet);

		for (String key : keySet) {
			String json = dataMap.get(key);

			try {
				ActionConfig actionConfig = new Gson().fromJson(json, ActionConfig.class);
				int actionType = actionConfig.getActionType();

				if (actionType > 0) {
					registerService(actionType, json);

					JobInfo job = new JobInfo.Builder(actionType, new ComponentName(context, loggingServiceMap.get(actionType)))
							.setRequiredNetworkType(actionConfig.getRequiredNetworkType())
							.setRequiresCharging(actionConfig.isRequiresCharging())
							.setPeriodic(actionConfig.getPeriod())
							.setRequiresDeviceIdle(actionConfig.isRequiresDeviceIdle())
							.setPersisted(actionConfig.isPersisted())
							.build();

					jobInfos.add(job);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}


		return jobInfos;
	}

	private void callUploadApi(int jobId) {

		final String userKey = sharedPreferences.getString(context.getString(R.string.shared_prefs_push_token), "");

		switch (jobId) {
			case ActionType.GATHER_APP_USAGE_LOG:

				eventBus.subscribe(RxEventBus.PARSING_APP_USAGE_FINISHED, this, new Consumer<Object>() {
					@Override
					public void accept(Object o) throws Exception {
						logService.updateAppUsageLog(userKey, (List<ApplicationLog>) o)
								.subscribe(new Consumer() {
									@Override
									public void accept(Object o) throws Exception {
										System.out.print(o);
									}
								}, new Consumer<Throwable>() {
									@Override
									public void accept(Throwable throwable) throws Exception {
										System.out.print(throwable);
									}
								}, new Action() {
									@Override
									public void run() throws Exception {
										clearDBData(ApplicationLog.class);
									}
								});
					}
				});
				break;

			case ActionType.GATHER_DEVICE_SECURITY_LOG:

				eventBus.subscribe(RxEventBus.PARSING_SECURITY_FINISHED, this, new Consumer<Object>() {
					@Override
					public void accept(Object o) throws Exception {
						logService.updateDeviceSecurityLog(userKey, (List<DeviceSecurityLevel>) o)
								.subscribe(new Consumer() {
									@Override
									public void accept(Object o) throws Exception {
										System.out.print(o);
									}
								}, new Consumer<Throwable>() {
									@Override
									public void accept(Throwable throwable) throws Exception {
										System.out.print(throwable);
									}
								}, new Action() {
									@Override
									public void run() throws Exception {
										clearDBData(DeviceSecurityLevel.class);
									}
								});
					}
				});
				break;

			case ActionType.GATHER_CALL_LOG:

				eventBus.subscribe(RxEventBus.PARSING_CALL_FINISHED, this, new Consumer<Object>() {
					@Override
					public void accept(Object o) throws Exception {
						logService.updateCallLog(userKey, (List<CallHistoryLog>) o)
								.subscribe(new Consumer() {
									@Override
									public void accept(Object o) throws Exception {
										System.out.print(o);
									}
								}, new Consumer<Throwable>() {
									@Override
									public void accept(Throwable throwable) throws Exception {
										System.out.print(throwable);
									}
								}, new Action() {
									@Override
									public void run() throws Exception {
										clearDBData(CallHistoryLog.class);
									}
								});
					}
				});
				break;

			case ActionType.GATHER_LOCATION_LOG:
				eventBus.subscribe(RxEventBus.PARSING_LOCATION_FINISHED, this, new Consumer<Object>() {
					@Override
					public void accept(Object o) throws Exception {
						logService.updateLocationLog(userKey, (List<LocationLog>) o)
								.subscribe(new Consumer() {
									@Override
									public void accept(Object o) throws Exception {
										System.out.print(o);
									}
								}, new Consumer<Throwable>() {
									@Override
									public void accept(Throwable throwable) throws Exception {
										System.out.print(throwable);
									}
								}, new Action() {
									@Override
									public void run() throws Exception {
										clearDBData(LocationLog.class);
									}
								});
					}
				});
				break;

			case ActionType.GATHER_MESSAGE_LOG:
				eventBus.subscribe(RxEventBus.PARSING_SMS_FINISHED, this, new Consumer<Object>() {
					@Override
					public void accept(Object o) throws Exception {
						logService.updateSMSLog(userKey, (List<MessageLog>) o)
								.subscribe(new Consumer() {
									@Override
									public void accept(Object o) throws Exception {
										System.out.print(o);
									}
								}, new Consumer<Throwable>() {
									@Override
									public void accept(Throwable throwable) throws Exception {
										System.out.print(throwable);
									}
								}, new Action() {
									@Override
									public void run() throws Exception {
										clearDBData(MessageLog.class);
									}
								});
					}
				});
				break;
		}

	}

	private void clearDBData(Class clazz) {
		Realm realm = Realm.getDefaultInstance();
		realm.beginTransaction();
		realm.delete(clazz);
		realm.commitTransaction();
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

			case ActionType.GATHER_DEVICE_SECURITY_LOG:
				loggingServiceMap.put(ActionType.GATHER_DEVICE_SECURITY_LOG,
						SecurityLevelLoggingService.class.getName());
				break;

			case ActionType.GATHER_MESSAGE_LOG:
				loggingServiceMap.put(ActionType.GATHER_MESSAGE_LOG,
						SMSLoggingService.class.getName());
				break;

			case ActionType.UPLOAD_LOG:
				if (!loggingServiceMap.containsKey(ActionType.UPLOAD_LOG)) {
					loggingServiceMap.put(ActionType.UPLOAD_LOG,
							UploadService.class.getName());
				}
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
