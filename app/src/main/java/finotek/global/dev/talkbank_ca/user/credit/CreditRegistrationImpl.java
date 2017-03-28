package finotek.global.dev.talkbank_ca.user.credit;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.base.mvp.BasePresenter;

/**
 * Created by magyeong-ug on 2017. 3. 27..
 */

class CreditRegistrationImpl extends BasePresenter<CreditRegistrationView> implements CreditRegistration {

	private Context context;
	private boolean isCaptureDone;

	CreditRegistrationImpl(Context context) {
			this.context = context;
	}

	@Override
	public void takePic(String path) {
		if (isCaptureDone) {
			if (!TextUtils.isEmpty(path)) {
				getMvpView().displayOnCaptureDone();
			} else {
				getMvpView().setBtnCaptureText(R.string.take_pic);
			}
		} else {
			// TODO finish
		}

	}

	@Override
	public void recapture() {
		getMvpView().unlockCamera();
		getMvpView().displayOnRecapture();
	}
}
