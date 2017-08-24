package finotek.global.dev.talkbank_ca.user.sign;

import android.support.v4.content.ContextCompat;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;

/**
 * Created by jungwon on 7/18/2017.
 */

public class TransferSignRegisterFragment extends BaseSignRegisterFragment {

	@Override
	void initView() {

		String name = TransactionDB.INSTANCE.getTxName();
		String moneyAsString = TransactionDB.INSTANCE.getTxMoney();

		String temp = getResources().getString(R.string.dialog_chat_send_transfer, name, moneyAsString);
		binding.tvInst.setText(temp);

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

			binding.ibSizeControl.setVisibility(View.GONE);
		});

		RxView.clicks(binding.ibNext)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					if (stepCount > 1) {
						stepSubject.onNext(3);
						stepSubject.onComplete();
					}
				});

	}
}