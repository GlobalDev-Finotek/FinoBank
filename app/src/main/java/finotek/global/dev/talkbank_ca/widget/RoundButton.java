package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.util.Converter;

public class RoundButton extends AppCompatButton {
    public RoundButton(Context context) {
        super(context);

        int padding = Converter.dpToPx(15);
        setPadding(padding, getPaddingTop(), padding, getPaddingBottom());
    }

    public RoundButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundButton, 0, 0);
        try {
            int buttonType = array.getInteger(R.styleable.RoundButton_buttonType, 0);
            setButtonType(ButtonType.valueOf(buttonType));

            int padding = Converter.pxToDp(14);
            setPadding(padding, getPaddingTop(), padding, getPaddingBottom());
        } finally {
            array.recycle();
        }
    }

    public void setButtonType(ButtonType buttonType) {
        switch(buttonType) {
            case Primary:
                setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_default_primary2));
                setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                break;
            case Danger:
                setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_default_danger));
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