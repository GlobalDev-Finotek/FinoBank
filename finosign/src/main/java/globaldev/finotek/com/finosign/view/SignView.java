package globaldev.finotek.com.finosign.view;

import android.support.annotation.DrawableRes;

/**
 * Created by magyeong-ug on 2017. 11. 14..
 */

public interface SignView {
	void setIconRes(@DrawableRes int resId);
	void initView();
	void clearCanvas();
	void showToast(String text);
	void setInstructionText(String txt);
}
