package globaldev.finotek.com.finosign.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import globaldev.finotek.com.finosign.R;
import globaldev.finotek.com.finosign.inject.FinoSign;
import globaldev.finotek.com.finosign.inject.exception.InvalidSignatureException;
import globaldev.finotek.com.finosign.inject.exception.NotFoundRegisteredSignException;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Consumer;


/**
 * Created by jungwon on 7/18/2017.
 */

public class SignValidationFragment extends BaseSignRegisterFragment {

	private OnSignValidationListener onSignValidationListener;

	public static Fragment newInstance() {

		Bundle args = new Bundle();

		SignValidationFragment fragment = new SignValidationFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	protected void setNextStepAction(int step) {
		switch (step) {

			case 2:
				setIconRes(R.drawable.btn_next);
				setInstructionText("");
				break;

			case 3:
				clearCanvas();
				setIconRes(R.drawable.btn_confirm_disable);
				setInstructionText(getString(R.string.sign_veri_hidden));
				break;

			case 4:
				setIconRes(R.drawable.btn_confirm);
				setInstructionText("");
				break;

			case 5:

				FinoSign.getInstance()
						.verify(
								regularSignWrapper.firstDatas.toString(),
								hiddenSignWrapper.firstDatas.toString())
						.subscribe(new Consumer<Boolean>() {
							@Override
							public void accept(Boolean isValid) throws Exception {

								if(onSignValidationListener != null) {
									onSignValidationListener.onValidation(isValid);
									init();
								}
							}
						}, new Consumer<Throwable>() {
							@Override
							public void accept(Throwable throwable) throws Exception {


								if (throwable instanceof CompositeException) {
									CompositeException ce = (CompositeException) throwable;
									if (isSignExceptionOccured(ce.getExceptions(), InvalidSignatureException.class)) {

										hiddenSignWrapper.clear();
										regularSignWrapper.clear();
										SignValidationFragment.this.clearCanvas();
										SignValidationFragment.this.setIconRes(R.drawable.btn_next_disable);
										stepCount = 2;
										stepSubject.onNext(stepCount);
										showToast("Invalid signature. Please try again");

									} else if (isSignExceptionOccured(ce.getExceptions(), NotFoundRegisteredSignException.class)) {
										init();
										showToast("Register a signature first");

									}
								}

							}
						});
				break;
		}
	}

	@Override
	protected void setOnTouchCount() {

		drawingCanvas.setOnCanvasTouchListener(new DrawingCanvas.OnCanvasTouchListener() {
			@Override
			public void onTouchStart() {
				if (stepCount % 2 == 1) {
					stepSubject.onNext(++stepCount);
				}
			}
		});

		RxView.clicks(ibNext)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(new Consumer<Object>() {
					@Override
					public void accept(Object aVoid) throws Exception {

						if (stepCount == 5) {
							stepSubject.onComplete();
						} else {
							stepSubject.onNext(++stepCount);
						}
					}
				});
	}

	@Override
	protected void setSignDataListener() {
		drawingCanvas.setOnDrawListener(new DrawingCanvas.OnDrawListener() {
			@Override
			public void onDraw(String strData) {

				if (stepCount == 2) {
					regularSignWrapper.firstDatas.append(strData);
				} else if (stepCount == 4) {
					hiddenSignWrapper.firstDatas.append(strData);
				}
			}
		});
	}

	public void setOnSignValidationListener(OnSignValidationListener onSignValidationListener) {
		this.onSignValidationListener = onSignValidationListener;
	}

	@Override
	public void initView() {
		super.initView();
		setIconRes(R.drawable.btn_next_disable);
		setInstructionText(getString(R.string.sign_mark_area));
	}

	public interface OnSignValidationListener {
		void onValidation(boolean isValid);
	}
}