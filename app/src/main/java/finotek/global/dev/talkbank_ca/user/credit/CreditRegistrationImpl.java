package finotek.global.dev.talkbank_ca.user.credit;

import android.content.Context;

import finotek.global.dev.talkbank_ca.base.mvp.BasePresenter;
import finotek.global.dev.talkbank_ca.model.CreditCard;

/**
 * Created by magyeong-ug on 2017. 3. 27..
 */

class CreditRegistrationImpl extends BasePresenter<CreditRegistrationView> implements CreditRegistration {

	private Context context;

	CreditRegistrationImpl(Context context) {
		this.context = context;
	}

	@Override
	public void takePic(String path) {
		// TODO finish
		getMvpView().displayCreditCardInfo(CreditCard.getMockData());
	}


}
