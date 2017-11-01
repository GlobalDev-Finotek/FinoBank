package finotek.global.dev.brazil.user;

import finotek.global.dev.brazil.base.mvp.IMvpView;
import finotek.global.dev.brazil.model.User;

/**
 * Created by magyeong-ug on 2017. 4. 6..
 */

public interface UserRegisterView extends IMvpView {
	void showLastUserData(User user);
}
