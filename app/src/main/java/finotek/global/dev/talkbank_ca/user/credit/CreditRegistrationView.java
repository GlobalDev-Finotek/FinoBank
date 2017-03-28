package finotek.global.dev.talkbank_ca.user.credit;

import finotek.global.dev.talkbank_ca.base.mvp.IMvpView;
import finotek.global.dev.talkbank_ca.model.CreditCard;

/**
 * Created by magyeong-ug on 2017. 3. 27..
 */

interface CreditRegistrationView extends IMvpView {
	void displayCreditCardInfo(CreditCard card);
	void displayOnCaptureDone();
	void displayOnRecapture();
	void unlockCamera();
	void setBtnCaptureText(int stringId);
}
