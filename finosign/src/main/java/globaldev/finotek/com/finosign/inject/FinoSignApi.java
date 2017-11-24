package globaldev.finotek.com.finosign.inject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by magyeong-ug on 2017. 11. 3..
 */

public interface FinoSignApi {
	@POST("sign")
	Flowable<SignRegisterResponse> register(@Body SignRegisterRequest request);

	@POST("verify")
	Flowable<SignVerifyResponse> verify(@Body SignVerifyRequest request);

	class SignRegisterRequest {
		public List<SignData> signs = new ArrayList<>();
		public String userKey;
	}

	class SignRegisterResponse {
		public String signId;
	}

	class SignVerifyRequest {
		public List<SignData> signs = new ArrayList<>();
		public int threshold;
		public String userKey;
	}

	class SignVerifyResponse {
		public boolean isValid;
	}
}
