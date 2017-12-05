package globaldev.finotek.com.finosign.inject;

import globaldev.finotek.com.finosign.inject.request.SignRegisterRequest;
import globaldev.finotek.com.finosign.inject.request.SignVerifyRequest;
import globaldev.finotek.com.finosign.inject.request.UserJoinRequest;
import globaldev.finotek.com.finosign.inject.response.BaseResponse;
import globaldev.finotek.com.finosign.inject.response.SignRegisterResponse;
import globaldev.finotek.com.finosign.inject.response.SignVerifyResponse;
import globaldev.finotek.com.finosign.inject.response.UserJoinResponse;
import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by magyeong-ug on 2017. 11. 3..
 */

interface FinoSignApi {
	@POST("companies/{companyId}/signs")
	Flowable<BaseResponse<SignRegisterResponse>> register(@Path("companyId")String companyId, @Body SignRegisterRequest request);

	@POST("companies/{companyId}/user")
	Flowable<BaseResponse<UserJoinResponse>> join(@Path("companyId")String companyId, @Body UserJoinRequest request);

	@POST("verify")
	Flowable<BaseResponse<SignVerifyResponse>> verify(@Body SignVerifyRequest request);

}
