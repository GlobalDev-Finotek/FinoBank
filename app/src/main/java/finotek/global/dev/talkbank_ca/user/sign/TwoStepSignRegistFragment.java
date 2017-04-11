package finotek.global.dev.talkbank_ca.user.sign;

import android.support.v4.content.ContextCompat;


import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;

/**
 * Created by magyeong-ug on 2017. 3. 29..
 */

public class TwoStepSignRegistFragment extends BaseSignRegisterFragment {

	@Override
	void initView() {
		binding.tvInst.setText("표시된 영역 안에 서명해 주세요.(1/2)");
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
				binding.tvInst.setText("표시된 영역 안에 서명해 주세요.(2/2)");
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

}
