package finotek.global.dev.talkbank_ca.user;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.base.mvp.BasePresenter;
import finotek.global.dev.talkbank_ca.model.User;
import io.realm.Realm;

/**
 * Created by magyeong-ug on 2017. 4. 6..
 */

public class UserRegisterImpl extends BasePresenter<UserRegisterView> implements UserRegister {

	private Realm realm;

	@Inject
	UserRegisterImpl(Realm realm) {
		this.realm = realm;
	}

	@Override
	public void saveUser(User user) {
		realm.beginTransaction();
		realm.insertOrUpdate(user);
		realm.commitTransaction();
	}


}
