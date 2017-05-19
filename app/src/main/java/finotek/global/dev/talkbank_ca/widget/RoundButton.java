package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.util.Converter;

public class RoundButton extends IconButton {
	private Context context;

	public RoundButton(Context context) {
		super(context, null, android.R.attr.borderlessButtonStyle);
		this.context = context;

		int padding = Converter.dpToPx(15);
		setPadding(padding, getPaddingTop(), padding, getPaddingBottom());

		setTypeface();
	}

	public RoundButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundButton, 0, 0);
		try {
			int buttonType = array.getInteger(R.styleable.RoundButton_buttonType, 0);
			setButtonType(ButtonType.valueOf(buttonType));

			int padding = Converter.pxToDp(15);
			setPadding(padding, getPaddingTop(), padding, getPaddingBottom());
		} finally {
			array.recycle();
		}

		setTypeface();
	}

	public void setButtonType(ButtonType buttonType) {
		switch (buttonType) {
			case Primary:
				setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_default_round_primary));
				setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
				break;
			case Danger:
				setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_default_round_danger));
				setTextColor(ContextCompat.getColor(getContext(), R.color.danger));
				break;
			case Info:
				setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_default_info));
				setTextColor(ContextCompat.getColor(getContext(), R.color.info));
				break;
		}

		invalidate();
		requestLayout();
	}

	private void setTypeface() {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSansKR-Medium-Hestia.otf");
		setTypeface(typeface);
	}

	public enum ButtonType {
		Primary, Danger, Info;

		static ButtonType valueOf(int btnType) {
			if (btnType == Primary.ordinal()) return Primary;
			if (btnType == Danger.ordinal()) return Danger;
			if (btnType == Info.ordinal()) return Info;
			return null;
		}
	}
}