package finotek.global.dev.talkbank_ca.user.credit;

import finotek.global.dev.talkbank_ca.base.mvp.IPresenter;

/**
 * Created by magyeong-ug on 2017. 3. 27..
 */

interface CreditRegistration extends IPresenter<CreditRegistrationView> {
	/**
	 * @param path 사진 저장된 경로
	 *             사진 촬영
	 */
	void takePic(String path);

}
