package globaldev.finotek.com.finosign.service;

import javax.inject.Inject;

import globaldev.finotek.com.finosign.FinoSignService;
import globaldev.finotek.com.finosign.SignType;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * Created by magyeong-ug on 2017. 11. 3..
 */

class FinoSignServiceImpl implements FinoSignService {


	@Inject
	FinoSignApi finoSignApi;

	FinoSignServiceImpl() {
		DaggerApiComponent.builder().build().inject(this);
	}

	@Override
	public Flowable<String> register(String data, int signType, String userKey) {
		FinoSignApi.SignRegisterRequest registerRequest = new FinoSignApi.SignRegisterRequest();
		registerRequest.data = data;
		registerRequest.signType = signType;
		registerRequest.userKey = userKey;

		return finoSignApi.register(registerRequest)
				.map(new Function<FinoSignApi.SignRegisterResponse, String>() {
					@Override
					public String apply(FinoSignApi.SignRegisterResponse signRegisterResponse) throws Exception {
						return signRegisterResponse.signId;
					}
				});
	}

	@Override
	public Flowable<Boolean> verify(String masterSignData, String hiddenSignData, String userKey) {
		FinoSignApi.SignVerifyRequest verifyRequest = new FinoSignApi.SignVerifyRequest();

		SignData hiddenSign = new SignData(SignType.HIDDEN, hiddenSignData);
		SignData masterSign = new SignData(SignType.MASTER, masterSignData);
		verifyRequest.signs.add(hiddenSign);
		verifyRequest.signs.add(masterSign);
		verifyRequest.userKey = userKey;

		return finoSignApi.verify(verifyRequest)
				.map(new Function<FinoSignApi.SignRegisterResponse, Boolean>() {
					@Override
					public Boolean apply(FinoSignApi.SignRegisterResponse signRegisterResponse) throws Exception {
						return null;
					}
				});
	}


}
