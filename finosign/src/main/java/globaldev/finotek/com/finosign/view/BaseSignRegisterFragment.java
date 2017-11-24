package globaldev.finotek.com.finosign.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import globaldev.finotek.com.finosign.R;
import globaldev.finotek.com.finosign.SignWrapper;
import globaldev.finotek.com.finosign.databinding.FragmentDrawBinding;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by magyeong-ug on 2017. 3. 29..
 */

abstract class BaseSignRegisterFragment extends Fragment implements SignView {

	protected int stepCount = 1;
	protected PublishSubject<Integer> stepSubject;
	protected SignWrapper regularSignWrapper = new SignWrapper();
	protected SignWrapper hiddenSignWrapper = new SignWrapper();
	protected FragmentDrawBinding binding;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_draw, container, false);

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

		return binding.getRoot();
	}

	@Override
	public void clearCanvas() {
		binding.drawingCanvas.clear();
	}

	protected abstract void setNextStepAction(int step);

	protected abstract void setOnTouchCount();

	public void init() {
		stepCount = 1;
		stepSubject.onNext(stepCount);
		hiddenSignWrapper.clear();
		regularSignWrapper.clear();
	}

	@Override
	public void setInstructionText(String txt) {
		binding.tvInst.setText(txt);
	}

	@Override
	public void setIconRes(int resId) {
		binding.ibNext.setBackground(ContextCompat.getDrawable(getActivity(), resId));
	}

	protected abstract void setSignDataListener();


	interface OnDoneClickListener {
		void onClick();
	}
}
