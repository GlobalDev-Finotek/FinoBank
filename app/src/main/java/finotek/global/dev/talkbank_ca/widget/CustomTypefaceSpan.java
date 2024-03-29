package finotek.global.dev.talkbank_ca.widget;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/**
 * Created by magyeong-ug on 17/04/2017.
 */

@SuppressLint("ParcelCreator")
public class CustomTypefaceSpan extends TypefaceSpan {
	private final Typeface mNewType;

	public CustomTypefaceSpan(Typeface type) {
		super("");
		mNewType = type;
	}

	public CustomTypefaceSpan(String family, Typeface type) {
		super(family);
		mNewType = type;
	}

	private static void applyCustomTypeFace(Paint paint, Typeface tf) {
		int oldStyle;
		Typeface old = paint.getTypeface();
		if (old == null) {
			oldStyle = 0;
		} else {
			oldStyle = old.getStyle();
		}

		int fake = oldStyle & ~tf.getStyle();
		if ((fake & Typeface.BOLD) != 0) {
			paint.setFakeBoldText(true);
		}

		if ((fake & Typeface.ITALIC) != 0) {
			paint.setTextSkewX(-0.25f);
		}

		paint.setTypeface(tf);
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		applyCustomTypeFace(ds, mNewType);
	}

	@Override
	public void updateMeasureState(TextPaint paint) {
		applyCustomTypeFace(paint, mNewType);
	}
}
