package globaldev.finotek.com.finosign;

import globaldev.finotek.com.finosign.inject.exception.AlreadyRegisteredException;
import globaldev.finotek.com.finosign.inject.exception.InvalidSignatureException;
import globaldev.finotek.com.finosign.inject.exception.NotFoundRegisteredSignException;
import io.reactivex.Flowable;

/**
 * Created by magyeong-ug on 2017. 11. 3..
 */

public interface FinoSignService {
	Flowable<String> register(String data1, String data2, int signType);

	void register(String data1, String data2, int signType, final OnRegisterListener onRegisterListener)
			throws InvalidSignatureException, AlreadyRegisteredException;

	Flowable<Boolean> verify(String masterSignData, String hiddenSignData);

	void verify(String masterSignData, String hiddenSignData, final OnVerifyListener onVerifyListener)
			throws InvalidSignatureException, NotFoundRegisteredSignException;


	interface OnRegisterListener {
		void onRegister(String signId);
	}

	interface OnVerifyListener {
		void onVerify(boolean isValid);
	}
}
