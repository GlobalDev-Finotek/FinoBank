package globaldev.finotek.com.finosign.inject;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import globaldev.finotek.com.finosign.BuildConfig;
import globaldev.finotek.com.finosign.FinoSignService;
import globaldev.finotek.com.finosign.R;
import globaldev.finotek.com.finosign.SignType;
import globaldev.finotek.com.finosign.inject.exception.AlreadyRegisteredException;
import globaldev.finotek.com.finosign.inject.exception.ExceptionCode;
import globaldev.finotek.com.finosign.inject.exception.InvalidSignatureException;
import globaldev.finotek.com.finosign.inject.exception.NotFoundRegisteredSignException;
import globaldev.finotek.com.finosign.inject.request.SignRegisterRequest;
import globaldev.finotek.com.finosign.inject.request.SignVerifyRequest;
import globaldev.finotek.com.finosign.inject.request.UserJoinRequest;
import globaldev.finotek.com.finosign.inject.response.BaseResponse;
import globaldev.finotek.com.finosign.inject.response.SignRegisterResponse;
import globaldev.finotek.com.finosign.inject.response.SignVerifyResponse;
import globaldev.finotek.com.finosign.inject.response.UserJoinResponse;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * Created by magyeong-ug on 2017. 11. 3..
 */

class FinoSignServiceImpl implements FinoSignService {

	@Inject
	FinoSignApi finoSignApi;

	@Inject
	@Named("userKey")
	String userKey;

	@Inject
	@Named("token")
	String token;

	private Context context;

	FinoSignServiceImpl(Context context) {
		this.context = context;

		DaggerApiComponent.builder().apiModule(new ApiModule(context))
				.build()
				.inject(this);

		if (TextUtils.isEmpty(userKey) || TextUtils.isEmpty(token)) {
			joinFinoSignService();
		}
	}

	private void saveUserKey(String userKey) {
		context.getSharedPreferences(context.getString(R.string.finosign_prefs), Context.MODE_PRIVATE)
				.edit()
				.putString(context.getString(R.string.finosign_userkey), userKey).apply();
	}

	private void saveToken(String token) {
		context.getSharedPreferences(context.getString(R.string.finosign_prefs), Context.MODE_PRIVATE)
				.edit()
				.putString(context.getString(R.string.finosign_token), token).apply();
	}

	private void joinFinoSignService() {

		UserJoinRequest userJoinRequest = new UserJoinRequest();
		userJoinRequest.appId = UUID.randomUUID().toString();

		finoSignApi.join(BuildConfig.COMPANY_ID, userJoinRequest)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new Consumer<BaseResponse<UserJoinResponse>>() {
					@Override
					public void accept(BaseResponse<UserJoinResponse> userJoinResponse) throws Exception {
						saveUserKey(userJoinResponse.data.userKey);
						saveToken(userJoinResponse.data.token);
					}
				});
	}

	private Consumer<Throwable> errorHandler = new Consumer<Throwable>() {
		@Override
		public void accept(Throwable e) throws Exception {

			if(e instanceof HttpException) {
				ResponseBody responseBody = ((HttpException) e).response().errorBody();

				if (responseBody != null) {
					String body = responseBody.string();
					BaseResponse baseResponse = new Gson().fromJson(body, BaseResponse.class);

					switch (baseResponse.statusCode) {
						case ExceptionCode.INVALID_SIGNATURE:
							throw new InvalidSignatureException();
						case ExceptionCode.ALREADY_REGISTERED:
							throw new AlreadyRegisteredException();
						case ExceptionCode.NOT_FOUND_REGISTERED_SIGNATURE:
							throw new NotFoundRegisteredSignException();
					}
				}
			}
		}
	};



	@Override
	public Flowable<String> register(String data1, String data2, int signType) {
		SignRegisterRequest registerRequest = new SignRegisterRequest();
		registerRequest.signs.add(new SignData(signType, data1));
		registerRequest.signs.add(new SignData(signType, data2));
		registerRequest.type = signType;
		registerRequest.userKey = userKey;

		return finoSignApi.register(BuildConfig.COMPANY_ID, registerRequest)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.doOnError(errorHandler)
				.map(new Function<BaseResponse<SignRegisterResponse>, String>() {
					@Override
					public String apply(BaseResponse<SignRegisterResponse> signRegisterResponse) throws Exception {
						SignRegisterResponse data = signRegisterResponse.data;
						return data.signId;
					}
				});
	}

	@Override
	public void register(final String data1, String data2, int signType, final OnRegisterListener onRegisterListener) {
		SignRegisterRequest registerRequest = new SignRegisterRequest();
		registerRequest.signs.add(new SignData(signType, data1));
		registerRequest.signs.add(new SignData(signType, data2));
		registerRequest.type = signType;
		registerRequest.userKey = userKey;

		finoSignApi.register(BuildConfig.COMPANY_ID, registerRequest)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.doOnError(errorHandler)
				.subscribe(new Consumer<BaseResponse<SignRegisterResponse>>() {
					@Override
					public void accept(BaseResponse<SignRegisterResponse> signRegisterResponse) throws Exception {
						SignRegisterResponse data = signRegisterResponse.data;
						onRegisterListener.onRegister(data.signId);
					}
				});
	}

	@Override
	public Flowable<Boolean> verify(String masterSignData, String hiddenSignData) {
		SignVerifyRequest verifyRequest = new SignVerifyRequest();

		SignData hiddenSign = new SignData(SignType.HIDDEN, hiddenSignData);
		SignData masterSign = new SignData(SignType.MASTER, masterSignData);
		verifyRequest.signs.add(hiddenSign);
		verifyRequest.signs.add(masterSign);
		verifyRequest.userKey = userKey;

		return finoSignApi.verify(verifyRequest)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.doOnError(errorHandler)
				.map(new Function<BaseResponse<SignVerifyResponse>, Boolean>() {
					@Override
					public Boolean apply(BaseResponse<SignVerifyResponse> signVerifyResponse) throws Exception {
						return signVerifyResponse.data.isValid;
					}
				});
	}

	@Override
	public void verify(String masterSignData, String hiddenSignData, final OnVerifyListener onVerifyListener) {
		SignVerifyRequest verifyRequest = new SignVerifyRequest();

		SignData hiddenSign = new SignData(SignType.HIDDEN, hiddenSignData);
		SignData masterSign = new SignData(SignType.MASTER, masterSignData);
		verifyRequest.signs.add(hiddenSign);
		verifyRequest.signs.add(masterSign);
		verifyRequest.userKey = userKey;

		finoSignApi.verify(verifyRequest)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.doOnError(errorHandler)
				.subscribe(new Consumer<BaseResponse<SignVerifyResponse>>() {
					@Override
					public void accept(BaseResponse<SignVerifyResponse> signVerifyResponseBaseResponse) throws Exception {
						onVerifyListener.onVerify(signVerifyResponseBaseResponse.data.isValid);
					}
				});
	}


}
