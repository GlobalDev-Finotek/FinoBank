package globaldev.finotek.com.finosign.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import globaldev.finotek.com.finosign.R;
import globaldev.finotek.com.finosign.SignType;
import globaldev.finotek.com.finosign.inject.FinoSign;
import globaldev.finotek.com.finosign.inject.exception.AlreadyRegisteredException;
import globaldev.finotek.com.finosign.inject.exception.InvalidSignatureException;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;


/**
 * Created by magyeong-ug on 2017. 3. 29..
 */

public class SignRegisterFragment extends BaseSignRegisterFragment {

	private OnSignRegisterListener onSignRegisterLister;

	public static Fragment newInstance() {

		Bundle args = new Bundle();

		SignRegisterFragment fragment = new SignRegisterFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void setNextStepAction(int step) {

		switch (step) {

			case 2:
				/* 일반 서명 1회 데이터 기록 */
				setIconRes(R.drawable.btn_next);
				setInstructionText("");
				break;

			case 3:
				/* 일반 서명 1회차 저장 */
				clearCanvas();
				setIconRes(R.drawable.btn_next_disable);
				setInstructionText(getString(R.string.sign_regi_step2));
				break;

			case 4:
				/* 일반 서명 2회차 기록 */
				setIconRes(R.drawable.btn_confirm);
				setInstructionText("");
				break;

			case 5:
				/* 저장된 1회차 서명과 2회차 서명 비교 검증 */
				FinoSign.getInstance()
						.register(
								regularSignWrapper.firstDatas.toString(),
								regularSignWrapper.secondDatas.toString(), SignType.MASTER)
						.subscribe(
								new Consumer<String>() {
									@Override
									public void accept(String signId) throws Exception {
										SignRegisterFragment.this.clearCanvas();
										SignRegisterFragment.this.setIconRes(R.drawable.btn_next_disable);
										SignRegisterFragment.this.setInstructionText(SignRegisterFragment.this.getString(R.string.sign_regi_hidden_step1));
									}
								},
								new Consumer<Throwable>() {
									@Override
									public void accept(Throwable throwable) throws Exception {

										regularSignWrapper.clear();
										if (throwable instanceof CompositeException) {
											CompositeException ce = (CompositeException) throwable;
											if (isSignExceptionOccured(ce.getExceptions(), InvalidSignatureException.class)) {

												setIconRes(R.drawable.btn_next_disable);
												setInstructionText(getString(R.string.sign_regi_step1));
												stepCount = 2;
												stepSubject.onNext(stepCount);
												clearCanvas();
												showToast("Invalid signature. Please try again");
											} else if (isSignExceptionOccured(ce.getExceptions(), AlreadyRegisteredException.class)) {

												setIconRes(R.drawable.btn_next_disable);
												setInstructionText(getString(R.string.sign_regi_hidden_step1));
												stepSubject.onNext(++stepCount);
												clearCanvas();
												showToast("Signature is already registered");

											}
										}

									}
								}
						);
				break;

			case 6:
				/* 히든 서명 1회 데이터 기록 */
				setIconRes(R.drawable.btn_next);
				setInstructionText("");
				break;

			case 7:
				clearCanvas();
				setIconRes(R.drawable.btn_next_disable);
				setInstructionText(getString(R.string.sign_regi_hidden_step2));
				break;

			case 8:
				setIconRes(R.drawable.btn_confirm);
				setInstructionText("");
				break;

			case 9:
				FinoSign.getInstance()
						.register(
								hiddenSignWrapper.firstDatas.toString(),
								hiddenSignWrapper.secondDatas.toString(),
								SignType.HIDDEN)
						.subscribe(
								new Consumer<String>() {
									@Override
									public void accept(String signId) throws Exception {
										stepCount += 1;
										stepSubject.onNext(stepCount);
									}
								},
								new Consumer<Throwable>() {
									@Override
									public void accept(Throwable throwable) throws Exception {
										hiddenSignWrapper.clear();

										if (throwable instanceof CompositeException) {
											CompositeException ce = (CompositeException) throwable;

											if (isSignExceptionOccured(ce.getExceptions(), InvalidSignatureException.class)) {
												clearCanvas();
												setInstructionText(getString(R.string.sign_regi_hidden_step1));
												setIconRes(R.drawable.btn_next_disable);
												stepCount = 6;
												stepSubject.onNext(stepCount);

												showToast("Invalid signature. Please try again");
											} else if (isSignExceptionOccured(ce.getExceptions(), AlreadyRegisteredException.class)) {
												stepSubject.onNext(++stepCount);
												clearCanvas();
												showToast("Signature is already registered");

											}
										}
									}
								},
								new Action() {
									@Override
									public void run() throws Exception {
										if (onSignRegisterLister != null) {
											onSignRegisterLister.onRegisterDone(true);
											init();
										}
									}
								}
						);


				break;

			case 10:
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

						if (stepCount == 10) {
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
			}
		});
	}

	public void setOnSignRegisterLister(OnSignRegisterListener onSignRegisterLister) {
		this.onSignRegisterLister = onSignRegisterLister;
	}

	@Override
	public void initView() {
		super.initView();
		setIconRes(R.drawable.btn_next_disable);
		setInstructionText(getString(R.string.sign_regi_step1));
	}

	public interface OnSignRegisterListener {
		void onRegisterDone(boolean isDone) throws InvalidSignatureException, AlreadyRegisteredException;
	}

}