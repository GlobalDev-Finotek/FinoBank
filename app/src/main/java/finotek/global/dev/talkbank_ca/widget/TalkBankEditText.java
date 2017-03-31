package finotek.global.dev.talkbank_ca.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;

import finotek.global.dev.talkbank_ca.R;

/**
 * Created by magyeong-ug on 2017. 3. 23..
 */

public class TalkBankEditText extends android.support.v7.widget.AppCompatEditText {

	private Context context;
	OnModeChangeListener onModeChangeListener = new OnModeChangeListener() {
		@Override
		public void onModeChange(MODE mode) {
			switch (mode) {

				case NORMAL:

					setBackground(ContextCompat.getDrawable(context, R.drawable.edt_normal));
					break;

				case DISABLED:
					setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
					setBackground(ContextCompat.getDrawable(context, R.drawable.edt_disabled));
					setHint("");
					setFocusable(false);
					setEnabled(false);

					break;

				case FOCUS:
					setBackground(ContextCompat.getDrawable(context, R.drawable.edt_focus));
					break;

				case ERROR:
					setBackground(ContextCompat.getDrawable(context, R.drawable.edt_error));
					break;
			}
		}
	};

	public TalkBankEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public TalkBankEditText(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public TalkBankEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init();

	}

	private void init() {
		setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		setOnFocusChangeListener((v, hasFocus) -> {
			if (hasFocus) {
				onModeChangeListener.onModeChange(MODE.FOCUS);
			} else {
				onModeChangeListener.onModeChange(MODE.NORMAL);
			}
		});

	}

	public void setErrFilter(boolean isErrorCondition) {
		if (isErrorCondition) {
			onModeChangeListener.onModeChange(MODE.ERROR);
		} else {
			onModeChangeListener.onModeChange(MODE.NORMAL);
		}

	}

	public void setMode(MODE mode) {
		onModeChangeListener.onModeChange(mode);
	}

	public enum MODE {
		NORMAL, DISABLED, FOCUS, ERROR
	}

	interface OnModeChangeListener {
		void onModeChange(MODE mode);
	}


}
