package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by magyeong-ug on 2017. 4. 1..
 */

public class TalkBankButton extends android.support.v7.widget.AppCompatButton {

	public TalkBankButton(Context context) {
		super(context);
		init(context);
	}

	public TalkBankButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TalkBankButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}


	private void init(Context context) {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSansKR-Medium-Hestia.otf");
		setTypeface(typeface);
	}

}
