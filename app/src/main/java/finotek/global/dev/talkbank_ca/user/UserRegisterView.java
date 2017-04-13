package finotek.global.dev.talkbank_ca.user;

import finotek.global.dev.talkbank_ca.base.mvp.IMvpView;
import finotek.global.dev.talkbank_ca.model.User;

/**
 * Created by magyeong-ug on 2017. 4. 6..
 */

public interface UserRegisterView extends IMvpView {
	void showLastUserData(User user);
}
