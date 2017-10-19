package finotek.global.dev.talkbank_ca.user.sign;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import d2r.checksign.lib.FinoSign;

/**
 * Created by magyeong-ug on 2017. 3. 29..
 */

public class HiddenSignFragment extends BaseSignRegisterFragment {


	@Override
	void initView() {
		String name = TransactionDB.INSTANCE.getTxName();
		String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
		String message = getString(R.string.registration_string_write_signature_step_1);


		if (name != null && !name.isEmpty() && TransactionDB.INSTANCE.isTransfer())
			message = getString(R.string.dialog_chat_send_transfer, name, moneyAsString);
			binding.tvInst.setText(message);
	}



	@Override
	void setNextStepAction(int step) {

		Log.d("stepAction", String.valueOf(step));

		switch (step) {

			case 2:
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_next));
				binding.tvInst.setText("");
				break;

			case 3:
				Log.d("FINOPASS", "sign is saved");
				MySignStorage.saveSign(this.getContext(), binding.drawingCanvas);
				saveSign(SIGN_FILENAME, firstDatas.toString());
				binding.drawingCanvas.clear();
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_next_disable));
				binding.tvInst.setText(getString(R.string.registration_string_write_signature_step_2));
				break;

			case 4:
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_confirm));
				binding.tvInst.setText("");
				break;

			case 5:
				binding.drawingCanvas.clear();
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_next_disable));
				binding.tvInst.setText(getString(R.string.registration_string_write_hidden_signature_1));
				break;

			case 6:
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_next));
				binding.tvInst.setText("");
				break;

			case 7:
				binding.drawingCanvas.clear();
				saveSign(HIDDENSIGN_FILENAME, secondDatas.toString());
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_next_disable));
				binding.tvInst.setText(getString(R.string.registration_string_write_hidden_signature_2));
				break;

			case 8:
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_confirm));
				binding.tvInst.setText("");
				break;

		}
	}

	@Override
	void setOnTouchCount() {
		binding.drawingCanvas.setOnCanvasTouchListener(() -> {
			if (stepCount % 2 == 1) {
				stepSubject.onNext(++stepCount);
			}
		});

		RxView.clicks(binding.ibNext)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {

					if (stepCount == 8) {
						stepSubject.onComplete();
					} else {
						stepSubject.onNext(++stepCount);
					}
				});
	}

	@Override
	protected void setSignDataListener() {
		binding.drawingCanvas.setOnDrawListener((strData) -> {

			if (stepCount == 2) {
				firstDatas.append(strData);
			} else if (stepCount == 6) {
				secondDatas.append(strData);
			}
		});
	}
}