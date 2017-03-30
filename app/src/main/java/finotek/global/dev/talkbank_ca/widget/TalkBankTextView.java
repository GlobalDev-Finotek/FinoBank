package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import finotek.global.dev.talkbank_ca.R;

/**
 * Created by magyeong-ug on 2017. 3. 30..
 */

public class TalkBankTextView extends android.support.v7.widget.AppCompatTextView {


	public TalkBankTextView(Context context) {
		super(context);
	}

	public TalkBankTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public TalkBankTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		int fontFlag;
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.TalkBankTextView,
				0, 0);

		try {
			fontFlag = a.getInteger(R.styleable.TalkBankTextView_custom_font, 0);
		} finally {
			a.recycle();
		}

		Typeface tf = null;

		if (fontFlag == 0) {
			tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/NotoSansKR-Light-Hestia.otf");
		} else if (fontFlag == 1) {
			tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/NotoSansKR-Medium-Hestia.otf");
		} else if (fontFlag == 2) {
			tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/NotoSansKR-Regular-Hestia.otf");
		}

		setTypeface(tf);

	}
}
