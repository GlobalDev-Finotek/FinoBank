package finotek.global.dev.talkbank_ca.user;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.base.mvp.BasePresenter;
import finotek.global.dev.talkbank_ca.model.DBHelper;
import finotek.global.dev.talkbank_ca.model.User;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by magyeong-ug on 2017. 4. 6..
 */

public class UserRegisterImpl extends BasePresenter<UserRegisterView> implements UserRegister {

	private DBHelper dbHelper;

	@Inject
	UserRegisterImpl(DBHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	@Override
	public void saveUser(User user) {
		dbHelper.insert(user);
	}

	@Override
	public void showLastUser() {
		dbHelper.get(User.class)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(users -> getMvpView().showLastUserData(users.last()));

	}


}
