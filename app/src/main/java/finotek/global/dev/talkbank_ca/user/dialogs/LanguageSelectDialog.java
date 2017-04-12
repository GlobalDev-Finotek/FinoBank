package finotek.global.dev.talkbank_ca.user.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.Window;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.DialogLanguageSelectBinding;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by magyeong-ug on 2017. 4. 12..
 */

public class LanguageSelectDialog extends Dialog {

	private Locale locale;
	private DialogLanguageSelectBinding binding;
	private OnDoneListener listener;

	public LanguageSelectDialog(@NonNull Context context) {
		super(context);
		initView(context);
	}

	public LanguageSelectDialog(@NonNull Context context, @StyleRes int themeResId) {
		super(context, themeResId);
		initView(context);
	}

	protected LanguageSelectDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		initView(context);
	}

	private void initView(Context context) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_language_select, null, false);
		setContentView(binding.getRoot());

		RxView.clicks(binding.btnCancel)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					this.dismiss();
				});


		binding.rbEnglish.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				locale = getLocale("en");
				binding.rbKorean.setChecked(false);

			} else {
				binding.rbKorean.setChecked(true);
			}
		});

		binding.rbKorean.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				locale = getLocale("ko");
				binding.rbEnglish.setChecked(false);

			} else {
				binding.rbEnglish.setChecked(true);
			}
		});

		RxView.clicks(binding.btnOk)
				.observeOn(AndroidSchedulers.mainThread())
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					if (listener != null) {
						dismiss();
						listener.onLocaleChange(locale.toLanguageTag());
					}
				});
	}


	private Locale getLocale(String localeStr) {
		Locale locale = new Locale(localeStr);
		Locale.setDefault(locale);
		return locale;
	}

	public void setDoneListener(OnDoneListener listener) {
		this.listener = listener;
	}

	@Override
	public void show() {
		this.setCancelable(false);
		this.setCanceledOnTouchOutside(false);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		super.show();
	}

	public interface OnDoneListener {
		void onLocaleChange(String locale);
	}

}
