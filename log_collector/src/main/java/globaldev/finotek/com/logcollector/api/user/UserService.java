package globaldev.finotek.com.logcollector.api.user;

import globaldev.finotek.com.logcollector.api.message.BaseRequest;
import globaldev.finotek.com.logcollector.api.message.BaseResponse;
import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by magyeong-ug on 26/04/2017.
 */

public interface UserService {

	@POST("init")
	Flowable<BaseResponse<UserInitResponse>> init(@Body BaseRequest<UserInitParam> userInitRequest);

	@PUT("token")
	Flowable<BaseResponse> updateToken();

}
