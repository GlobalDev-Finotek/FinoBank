package finotek.global.dev.talkbank_ca;

import android.app.Activity;
import android.content.Intent;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.base.mvp.BasePresenter;
import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.user.UserRegistrationActivity;
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
	 * @param isFirst - 앱을 처음 실행해는 지 여부
	 *                처음 앱 실행여부에 따라 화면 전환
	 */
	@Override
	public void moveToNextActivity(boolean isFirst) {

		Intent intent;

		if (isFirst) {
			intent = new Intent(activity, UserRegistrationActivity.class);
			sharedPrefsHelper.put("isFirst", false);
		} else {
			intent = new Intent(activity, ChatActivity.class);
		}

		activity.startActivity(intent);
	}

}
