package finotek.global.dev.brazil.user.credit;

import finotek.global.dev.brazil.base.mvp.IMvpView;
import finotek.global.dev.brazil.model.CreditCard;

/**
 * Created by magyeong-ug on 2017. 3. 27..
 */

interface CreditRegistrationView extends IMvpView {
	void displayCreditCardInfo(CreditCard card);

}
