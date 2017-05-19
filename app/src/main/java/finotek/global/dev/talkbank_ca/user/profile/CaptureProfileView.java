package finotek.global.dev.talkbank_ca.user.profile;

import android.graphics.Matrix;
import android.view.Surface;

import finotek.global.dev.talkbank_ca.base.mvp.IMvpView;

/**
 * Created by kwm on 2017. 3. 6..
 */

interface CaptureProfileView extends IMvpView {
	void setFlashButtonIcon(boolean flashState);

	void stopRecording(String videoFilePath);

	void setProgress(int progress);

	void requestVideoPermissions();

	void setTransform(Matrix matrix);

	boolean isAvailableTextureView();

	Surface getRecordingSurface(int width, int height);
}
