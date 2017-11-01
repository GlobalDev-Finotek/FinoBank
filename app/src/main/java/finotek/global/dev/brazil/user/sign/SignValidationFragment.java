package finotek.global.dev.brazil.user.sign;

import android.support.v4.content.ContextCompat;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.brazil.R;

/**
 * Created by jungwon on 7/18/2017.
 */

public class SignValidationFragment extends BaseSignRegisterFragment {

	@Override
	void initView() {
		String temp = getResources().getString(R.string.setting_string_write_signature);
		binding.tvInst.setText(temp);
		binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_confirm_disable));
	}

	@Override
	void setNextStepAction(int step) {
		switch (step) {

			case 2:
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_next));
				binding.tvInst.setText("");
				break;

			case 3:
				binding.drawingCanvas.clear();
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_confirm_disable));
				binding.tvInst.setText(getString(R.string.registration_string_write_hidden_signature));
				break;

			case 4:
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_confirm));
				binding.tvInst.setText("");
				break;
		}
	}

	@Override
	void setOnTouchCount() {

		binding.drawingCanvas.setOnCanvasTouchListener(() -> {
			if (stepCount == 1 || stepCount == 3) {
				stepSubject.onNext(++stepCount);
			}
			binding.ibSizeControl.setVisibility(View.GONE);
		});

		RxView.clicks(binding.ibNext)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {

					if (stepCount == 2) {
						stepSubject.onNext(++stepCount);
					} else if (stepCount == 4) {
						stepSubject.onComplete();
					}
				});
	}

	@Override
	protected void setSignDataListener() {
		binding.drawingCanvas.setOnDrawListener((strData) -> {

			if (stepCount == 2) {
				regularSignWrapper.firstDatas.append(strData);
			} else if (stepCount == 4) {
				hiddenSignWrapper.firstDatas.append(strData);
			}
		});
	}
}