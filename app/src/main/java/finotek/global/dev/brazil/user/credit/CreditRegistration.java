package finotek.global.dev.brazil.user.credit;

import finotek.global.dev.brazil.base.mvp.IPresenter;

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
