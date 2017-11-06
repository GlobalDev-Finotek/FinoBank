package globaldev.finotek.com.finosign.service;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by magyeong-ug on 2017. 11. 3..
 */

interface FinoSignApi {
	@POST("/sign")
	Flowable<SignRegisterResponse> register(@Body SignRegisterRequest request);

	@POST("/verify")
	Flowable<SignRegisterResponse> verify(@Body SignVerifyRequest request);

	class SignRegisterRequest {
		public String data;
		public int signType;
		public String userKey;
	}

	class SignRegisterResponse {
		public String signId;
	}

	class SignVerifyRequest {
		public List<SignData> signs = new ArrayList<>();
		public String userKey;
	}

	class SignVerifyResponse {
		public String masterSignData;
		public String hiddenSignData;
		public String userKey;
	}
}
