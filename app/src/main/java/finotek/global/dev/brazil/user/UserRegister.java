package finotek.global.dev.brazil.user;

import finotek.global.dev.brazil.model.User;

/**
 * Created by magyeong-ug on 2017. 4. 6..
 */

public interface UserRegister {
	void saveUser(User user);

	void showLastUser();
}
