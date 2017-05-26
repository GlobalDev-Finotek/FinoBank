package globaldev.finotek.com.logcollector.api.log;

import globaldev.finotek.com.logcollector.api.message.BaseRequest;
import globaldev.finotek.com.logcollector.api.message.BaseResponse;
import globaldev.finotek.com.logcollector.model.ApplicationLog;
import globaldev.finotek.com.logcollector.model.CallHistoryLog;
import globaldev.finotek.com.logcollector.model.DeviceSecurityLevel;
import globaldev.finotek.com.logcollector.model.LocationLog;
import globaldev.finotek.com.logcollector.model.MessageLog;
import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.PUT;

/**
 * Created by magyeong-ug on 26/04/2017.
 */

public interface LogApi {

	@PUT("log")
	Flowable<BaseResponse>
	updateDeviceSecurityLog(@Body BaseRequest<ApiServiceImpl.UploadLogParam<DeviceSecurityLevel>> logs);


	@PUT("log")
	Flowable<BaseResponse>
	updateAppUsageLog(@Body BaseRequest<ApiServiceImpl.UploadLogParam<ApplicationLog>> logs);


	@PUT("log")
	Flowable<BaseResponse>
	updateSMSLog(@Body BaseRequest<ApiServiceImpl.UploadLogParam<MessageLog>> logs);

	@PUT("log")
	Flowable<BaseResponse>
	updateCallLog(@Body BaseRequest<ApiServiceImpl.UploadLogParam<CallHistoryLog>> logs);

	@PUT("log")
	Flowable<BaseResponse>
	updateLocationLog(@Body BaseRequest<ApiServiceImpl.UploadLogParam<LocationLog>> logs);
}
