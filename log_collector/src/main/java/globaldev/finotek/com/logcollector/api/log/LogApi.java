package globaldev.finotek.com.logcollector.api.log;

import globaldev.finotek.com.logcollector.api.message.BaseRequest;
import globaldev.finotek.com.logcollector.api.message.BaseResponse;
import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by magyeong-ug on 26/04/2017.
 */

public interface LogApi {


	@POST("logs")
	Flowable<BaseResponse>
	update(@Body BaseRequest logs);

	@POST("recents")
	Flowable<BaseResponse>
	getRecentLogs(@Body BaseRequest logs);

//	@PUT("log")
//	Flowable<BaseResponse>
//	updateDeviceSecurityLog(@Body BaseRequest<ApiServiceImpl.UploadLogParam<DeviceSecurityLevel>> logs);
//
//
//	@PUT("log")
//	Flowable<BaseResponse>
//	updateAppUsageLog(@Body BaseRequest<ApiServiceImpl.UploadLogParam<ApplicationLog>> logs);
//
//
//	@PUT("log")
//	Flowable<BaseResponse>
//	updateSMSLog(@Body BaseRequest<ApiServiceImpl.UploadLogParam<MessageLog>> logs);
//
//	@PUT("log")
//	Flowable<BaseResponse>
//	updateCallLog(@Body BaseRequest<ApiServiceImpl.UploadLogParam<CallHistoryLog>> logs);
//
//	@PUT("log")
//	Flowable<BaseResponse>
//	updateLocationLog(@Body BaseRequest<ApiServiceImpl.UploadLogParam<LocationLog>> logs);
}
