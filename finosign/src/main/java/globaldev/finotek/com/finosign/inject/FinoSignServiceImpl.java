package globaldev.finotek.com.finosign.inject;

import javax.inject.Inject;

import globaldev.finotek.com.finosign.BuildConfig;
import globaldev.finotek.com.finosign.FinoSignService;
import globaldev.finotek.com.finosign.SignType;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by magyeong-ug on 2017. 11. 3..
 */

class FinoSignServiceImpl implements FinoSignService {

	@Inject
	FinoSignApi finoSignApi;

	FinoSignServiceImpl() {
		DaggerApiComponent.builder().build().inject(this );
	}

	@Override
	public Flowable<String> register(String data1, String data2, int signType) {
		FinoSignApi.SignRegisterRequest registerRequest = new FinoSignApi.SignRegisterRequest();
		registerRequest.signs.add(new SignData(signType, data1));
		registerRequest.signs.add(new SignData(signType, data2));
		registerRequest.userKey = BuildConfig.FINOSIGN_USER_KEY;

		return finoSignApi.register(registerRequest)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.map(new Function<FinoSignApi.SignRegisterResponse, String>() {
					@Override
					public String apply(FinoSignApi.SignRegisterResponse signRegisterResponse) throws Exception {
						return signRegisterResponse.signId;
					}
				});
	}

	@Override
	public void register(String data1, String data2, int signType, final OnRegisterListener onRegisterListener) {
		FinoSignApi.SignRegisterRequest registerRequest = new FinoSignApi.SignRegisterRequest();
		registerRequest.signs.add(new SignData(signType, data1));
		registerRequest.signs.add(new SignData(signType, data2));
		registerRequest.userKey = BuildConfig.FINOSIGN_USER_KEY;

		finoSignApi.register(registerRequest)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new Consumer<FinoSignApi.SignRegisterResponse>() {
					@Override
					public void accept(FinoSignApi.SignRegisterResponse signRegisterResponse) throws Exception {
						onRegisterListener.onRegister(signRegisterResponse.signId);
					}
				});
	}

	@Override
	public Flowable<Boolean> verify(String masterSignData, String hiddenSignData) {
		FinoSignApi.SignVerifyRequest verifyRequest = new FinoSignApi.SignVerifyRequest();

		SignData hiddenSign = new SignData(SignType.HIDDEN, hiddenSignData);
		SignData masterSign = new SignData(SignType.MASTER, masterSignData);
		verifyRequest.signs.add(hiddenSign);
		verifyRequest.signs.add(masterSign);
		verifyRequest.userKey = BuildConfig.FINOSIGN_USER_KEY;

		return finoSignApi.verify(verifyRequest)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.map(new Function<FinoSignApi.SignVerifyResponse, Boolean>() {
					@Override
					public Boolean apply(FinoSignApi.SignVerifyResponse signVerifyResponse) throws Exception {
						boolean isValid = signVerifyResponse.isValid;

						if(isValid) return isValid;
						else {
							throw new Exception();
						}

					}
				});
	}

	@Override
	public void verify(String masterSignData, String hiddenSignData, final OnVerifyListener onVerifyListener) {
		FinoSignApi.SignVerifyRequest verifyRequest = new FinoSignApi.SignVerifyRequest();

		SignData hiddenSign = new SignData(SignType.HIDDEN, hiddenSignData);
		SignData masterSign = new SignData(SignType.MASTER, masterSignData);
		verifyRequest.signs.add(hiddenSign);
		verifyRequest.signs.add(masterSign);
		verifyRequest.userKey = BuildConfig.FINOSIGN_USER_KEY;

		finoSignApi.verify(verifyRequest)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new Consumer<FinoSignApi.SignVerifyResponse>() {
					@Override
					public void accept(FinoSignApi.SignVerifyResponse signVerifyResponse) throws Exception {
						onVerifyListener.onVerify(signVerifyResponse.isValid);
					}
				});
	}



}
