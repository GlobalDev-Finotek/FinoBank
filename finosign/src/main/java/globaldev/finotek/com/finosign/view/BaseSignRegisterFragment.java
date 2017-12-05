package globaldev.finotek.com.finosign.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import globaldev.finotek.com.finosign.R;
import globaldev.finotek.com.finosign.SignWrapper;
import globaldev.finotek.com.finosign.inject.exception.InvalidSignatureException;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by magyeong-ug on 2017. 3. 29..
 */

abstract class BaseSignRegisterFragment extends Fragment implements SignView {

	int stepCount = 1;
	PublishSubject<Integer> stepSubject;
	SignWrapper regularSignWrapper = new SignWrapper();
	SignWrapper hiddenSignWrapper = new SignWrapper();
	private TextView tvInst;
	ImageButton ibNext;
	DrawingCanvas drawingCanvas;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_draw, null);
		tvInst = (TextView) v.findViewById(R.id.tv_inst);
		ibNext = (ImageButton) v.findViewById(R.id.ib_next);
		drawingCanvas = (DrawingCanvas) v.findViewById(R.id.drawingCanvas);

		initView();

		stepSubject = PublishSubject.create();

		setOnTouchCount();

		setSignDataListener();

		stepSubject
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						new Consumer<Integer>() {
							@Override
							public void accept(Integer step) throws Exception {
								setNextStepAction(step);
							}
						},
						new Consumer<Throwable>() {
							@Override
							public void accept(Throwable throwable) throws Exception {
							}
						},
						new Action() {
							@Override
							public void run() throws Exception {

							}
						});

		return v.getRootView();
	}

	@Override
	public void clearCanvas() {
		drawingCanvas.clear();
	}

	protected abstract void setNextStepAction(int step);

	protected abstract void setOnTouchCount();

	public void init() {
		stepCount = 1;
		stepSubject.onNext(stepCount);
		hiddenSignWrapper.clear();
		regularSignWrapper.clear();
		clearCanvas();
		initView();
	}

	@Override
	public void initView() {
	}

	@Override
	public void setInstructionText(String txt) {
		tvInst.setText(txt);
	}

	@Override
	public void setIconRes(int resId) {
		ibNext.setBackground(ContextCompat.getDrawable(getActivity(), resId));
	}

	protected abstract void setSignDataListener();

	protected boolean isSignExceptionOccured(List<Throwable> exceptionList, Class<?> cls) {
		Throwable t = findException(exceptionList, cls);
		return t != null;
	}

	protected Throwable findException(List<Throwable> exceptionList, Class<?> cls) {

		for (Throwable t : exceptionList) {
			if (cls.isInstance(t)) {
				return t;
			}
		}
		return new IllegalStateException();
	}

	@Override
	public void showToast(String text) {
		Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
	}

	interface OnDoneClickListener {
		void onClick();
	}
}
