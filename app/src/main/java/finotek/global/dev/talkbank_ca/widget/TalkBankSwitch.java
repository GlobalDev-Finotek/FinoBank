package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.Switch;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.util.Converter;

/**
 * Created by magyeong-ug on 2017. 3. 30..
 */

public class TalkBankSwitch extends Switch {
	public TalkBankSwitch(Context context) {
		super(context);
		init(context);
	}

	public TalkBankSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TalkBankSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public TalkBankSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	private void init(Context context) {
		setMinimumHeight(Converter.dpToPx(30));
		setSwitchMinWidth(Converter.dpToPx(58));
		setTrackDrawable(ContextCompat.getDrawable(context, R.drawable.switch_track_off));
		setThumbDrawable(ContextCompat.getDrawable(context, R.drawable.switch_thumb_selector));
		setThumbTextPadding((int) context.getResources().getDimension(R.dimen.switch_thumb_radius));
	}
}
