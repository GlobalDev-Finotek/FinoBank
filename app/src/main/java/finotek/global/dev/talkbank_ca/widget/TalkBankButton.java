package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import finotek.global.dev.talkbank_ca.R;

public class TalkBankButton extends android.support.v7.widget.AppCompatButton {

	public TalkBankButton(Context context) {
		super(context);

		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/NotoSansKR-Light-Hestia.otf");
		setTypeface(tf);
	}

	public TalkBankButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public TalkBankButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}


	private void init(Context context, AttributeSet attrs) {
		int fontFlag;
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.TalkBankButton,
				0, 0);

		try {
			fontFlag = a.getInteger(R.styleable.TalkBankButton_tb_font, 0);
		} finally {
			a.recycle();
		}

		Typeface tf = null;

		if (fontFlag == 0) {
			tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/NotoSansKR-Regular-Hestia.otf");
		} else if (fontFlag == 1) {
			tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/NotoSansKR-Medium-Hestia.otf");
		} else if (fontFlag == 2) {
			tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/NotoSansKR-Light-Hestia.otf");
		}

		setTypeface(tf);
	}

	public void setFontType(TalkBankButton.FontType fontType) {
		Typeface tf = null;

		switch (fontType) {
			case Regular:
				tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/NotoSansKR-Regular-Hestia.otf");
				break;
			case Medium:
				tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/NotoSansKR-Medium-Hestia.otf");
				break;
			case Light:
				tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/NotoSansKR-Light-Hestia.otf");
				break;
		}

		setTypeface(tf);
	}

	public enum FontType {
		Light, Medium, Regular
	}
}
