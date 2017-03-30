package finotek.global.dev.talkbank_ca;

import android.app.Activity;
import android.content.Intent;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.base.mvp.BasePresenter;
import finotek.global.dev.talkbank_ca.user.UserRegistrationActivity;
import finotek.global.dev.talkbank_ca.user.sign.SignRegistrationActivity;
import finotek.global.dev.talkbank_ca.util.SharedPrefsHelper;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

public class MainPresenterImpl extends BasePresenter<MainView> implements MainPresenter {

	@Inject
	SharedPrefsHelper sharedPrefsHelper;
	private Activity activity;

	@Inject
	MainPresenterImpl(Activity activity) {
		this.activity = activity;
	}

	/**
	 * 사용자 인증 여부에 따른 화면 전환
	 */
	@Override
	public void moveToNextActivity(boolean isFirst) {

		Intent intent;

		if (isFirst) {
			intent = new Intent(activity, UserRegistrationActivity.class);
		} else {
			intent = new Intent(activity, SignRegistrationActivity.class);
		}

		activity.startActivity(intent);
	}

}
