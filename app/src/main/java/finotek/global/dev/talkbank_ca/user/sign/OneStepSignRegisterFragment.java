package finotek.global.dev.talkbank_ca.user.sign;

import android.support.v4.content.ContextCompat;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;

/**
 * Created by magyeong-ug on 2017. 4. 6..
 */

public class OneStepSignRegisterFragment extends BaseSignRegisterFragment {
	@Override
	void initView() {
		binding.tvInst.setText(getString(R.string.registration_string_fits_area_take_picture));
		binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_confirm_disable));
	}

	@Override
	void setNextStepAction(int step) {
		switch (step) {
			case 2:
				binding.tvInst.setText("");
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_confirm));
				break;
		}
	}

	@Override
	void setOnTouchCount() {

		binding.drawingCanvas.setOnCanvasTouchListener(() -> {
			if (stepCount == 1) {
				stepSubject.onNext(++stepCount);
			}
		});

		RxView.clicks(binding.ibNext)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					stepSubject.onNext(3);
					stepSubject.onComplete();
				});

	}
}
