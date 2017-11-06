package finotek.global.dev.talkbank_ca.user.sign;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.user.dialogs.WarningDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by magyeong-ug on 2017. 3. 29..
 */

public class SignRegisterFragment extends BaseSignRegisterFragment {


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
				/* 일반 서명 1회 데이터 기록 */
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_next));
				binding.tvInst.setText("");
				break;

			case 3:
				/* 일반 서명 1회차 저장 */
				Log.d("FINOPASS", "sign is saved");
				MySignStorage.saveSign(this.getContext(), binding.drawingCanvas);
				saveSign(SIGN_FILENAME, regularSignWrapper.firstDatas.toString());
				binding.drawingCanvas.clear();
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_next_disable));
				binding.tvInst.setText(getString(R.string.registration_string_write_signature_step_2));
				break;

			case 4:
				/* 일반 서명 2회차 기록 */
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_confirm));
				binding.tvInst.setText("");
				break;

			case 5:
				/* 저장된 1회차 서명과 2회차 서명 비교 검증 */
				int similarity = 0;
				if (similarity / 100 < 50) {

					WarningDialog warningDialog = new WarningDialog(getContext());
					warningDialog.setTitle(getString(R.string.setting_string_click_re_try_button));
					warningDialog.setButtonText(getString(R.string.setting_string_re_try));
					warningDialog.setDoneListener(() -> {
						regularSignWrapper.clear();
						binding.drawingCanvas.clear();
						stepCount -= 1;
						stepSubject.onNext(stepCount);
						warningDialog.dismiss();
					});

					warningDialog.show();
				}

				binding.drawingCanvas.clear();
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_next_disable));
				binding.tvInst.setText(getString(R.string.registration_string_write_hidden_signature_1));
				break;

			case 6:
				/* 히든 서명 1회 데이터 기록 */
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_next));
				binding.tvInst.setText("");
				break;

			case 7:
				binding.drawingCanvas.clear();
				Log.d("FINOPASS", "hidden sign is saved");
				saveSign(HIDDENSIGN_FILENAME, hiddenSignWrapper.firstDatas.toString());
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_next_disable));
				binding.tvInst.setText(getString(R.string.registration_string_write_hidden_signature_2));
				break;

			case 8:
				binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_confirm));
				binding.tvInst.setText("");
				break;

			case 9:

				int hiddenSimilarity = 0;
				if (hiddenSimilarity / 100 < 50) {

					WarningDialog warningDialog = new WarningDialog(getContext());
					warningDialog.setTitle(getString(R.string.setting_string_click_re_try_button));
					warningDialog.setButtonText(getString(R.string.setting_string_re_try));
					warningDialog.setDoneListener(() -> {
						hiddenSignWrapper.clear();
						binding.drawingCanvas.clear();
						stepCount -= 1;
						stepSubject.onNext(stepCount);
						warningDialog.dismiss();
					});

					warningDialog.show();
				} else {
					stepCount += 1;
					stepSubject.onNext(stepCount);
				}
				break;

			case 10:
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

					if (stepCount == 10) {
						stepSubject.onComplete();
					} else {
						stepSubject.onNext(++stepCount);
					}
				});
	}

	@Override
	protected void setSignDataListener() {
		binding.drawingCanvas.setOnDrawListener((strData) -> {

			switch (stepCount) {
				case 2:
					regularSignWrapper.firstDatas.append(strData);
					break;
				case 4:
					regularSignWrapper.secondDatas.append(strData);
					break;
				case 6:
					hiddenSignWrapper.firstDatas.append(strData);
					break;
				case 8:
					hiddenSignWrapper.secondDatas.append(strData);
					break;
			}
		});
	}
}