package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.util.Converter;
import finotek.global.dev.talkbank_ca.util.TBFonts;

public class TBTextView extends AppCompatTextView  {
    public TBTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundButton, 0, 0);
        try {
            int fontType = array.getInteger(R.styleable.TBTextView_tbFontType, 0);
            setFontType(context, fontType);

            int padding = Converter.pxToDp(14);
            setPadding(padding, getPaddingTop(), padding, getPaddingBottom());
        } finally {
            array.recycle();
        }
    }

    public void setFontType(Context context, int fontType){
        switch (fontType) {
            case 0: // normal
                setTypeface(TBFonts.normalTypeFace(context));
                break;
            case 1: // medium
                setTypeface(TBFonts.mediumTypeFace(context));
                break;
            case 2: // light
                setTypeface(TBFonts.lightTypeFace(context));
                break;
        }

        invalidate();
        requestLayout();
    }


}
