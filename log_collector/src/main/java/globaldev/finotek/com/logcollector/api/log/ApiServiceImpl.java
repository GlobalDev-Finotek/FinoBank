package globaldev.finotek.com.logcollector.api.log;

import java.util.List;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.api.message.BaseRequest;
import globaldev.finotek.com.logcollector.api.message.BaseResponse;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.model.ApplicationLog;
import globaldev.finotek.com.logcollector.model.CallHistoryLog;
import globaldev.finotek.com.logcollector.model.DeviceSecurityLevel;
import globaldev.finotek.com.logcollector.model.LocationLog;
import globaldev.finotek.com.logcollector.model.MessageLog;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by magyeong-ug on 27/04/2017.
 */

public class ApiServiceImpl {

	@Inject
	LogApi logService;


	@Inject
	public ApiServiceImpl(LogApi logService) {
		this.logService = logService;
	}

	public Flowable<BaseResponse> updateCallLog(String userKey, List<CallHistoryLog> logs) {

		UploadLogParam<CallHistoryLog> historyLogUploadLogParam = new UploadLogParam<>();
		historyLogUploadLogParam.userKey = userKey;
		historyLogUploadLogParam.logs = logs;
		historyLogUploadLogParam.type = ActionType.GATHER_CALL_LOG;

		BaseRequest<UploadLogParam<CallHistoryLog>> updateLogRequest =
				new BaseRequest.Builder<UploadLogParam<CallHistoryLog>>()
						.setParam(historyLogUploadLogParam)
						.build();


		return logService.updateCallLog(updateLogRequest)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}


	public Flowable<BaseResponse> updateLocationLog(String userKey, List<LocationLog> logs) {

		UploadLogParam<LocationLog> historyLogUploadLogParam = new UploadLogParam<>();
		historyLogUploadLogParam.userKey = userKey;
		historyLogUploadLogParam.logs = logs;
		historyLogUploadLogParam.type = ActionType.GATHER_LOCATION_LOG;

		BaseRequest<UploadLogParam<LocationLog>> updateLogRequest =
				new BaseRequest.Builder<UploadLogParam<LocationLog>>()
						.setParam(historyLogUploadLogParam)
						.build();
		return logService.updateLocationLog(updateLogRequest)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	public Flowable<BaseResponse> updateAppUsageLog(String userKey, List<ApplicationLog> logs) {

		UploadLogParam<ApplicationLog> applicationLogUploadLogParam = new UploadLogParam<>();
		applicationLogUploadLogParam.userKey = userKey;
		applicationLogUploadLogParam.logs = logs;
		applicationLogUploadLogParam.type = ActionType.GATHER_APP_USAGE_LOG;

		BaseRequest<UploadLogParam<ApplicationLog>> updateLogRequest =
				new BaseRequest.Builder<UploadLogParam<ApplicationLog>>()
						.setParam(applicationLogUploadLogParam)
						.build();
		return logService.updateAppUsageLog(updateLogRequest)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread());
	}

	public Flowable<BaseResponse> updateDeviceSecurityLog(String userKey, List<DeviceSecurityLevel> logs) {

		UploadLogParam<DeviceSecurityLevel> deviceSecureLogParam = new UploadLogParam<>();
		deviceSecureLogParam.userKey = userKey;
		deviceSecureLogParam.logs = logs;
		deviceSecureLogParam.type = ActionType.GATHER_DEVICE_SECURITY_LOG;

		BaseRequest<UploadLogParam<DeviceSecurityLevel>> updateLogRequest =
				new BaseRequest.Builder<UploadLogParam<DeviceSecurityLevel>>()
						.setParam(deviceSecureLogParam)
						.build();
		return logService.updateDeviceSecurityLog(updateLogRequest)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread());
	}

	public Flowable<BaseResponse> updateSMSLog(String userKey, List<MessageLog> logs) {

		UploadLogParam<MessageLog> messageLogUploadLogParam = new UploadLogParam<>();
		messageLogUploadLogParam.type = ActionType.GATHER_MESSAGE_LOG;
		messageLogUploadLogParam.userKey = userKey;
		messageLogUploadLogParam.logs = logs;

		BaseRequest<UploadLogParam<MessageLog>> updateLogRequest =
				new BaseRequest.Builder<UploadLogParam<MessageLog>>()
						.setParam(messageLogUploadLogParam)
						.build();
		return logService.updateSMSLog(updateLogRequest)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread());
	}


	class UploadLogParam<T> {
		int type;
		String userKey;
		List<T> logs;
	}
}
