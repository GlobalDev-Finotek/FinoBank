package finotek.global.dev.talkbank_ca.user.sign;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.FragmentDrawBinding;
import d2r.checksign.lib.FinoSign;
import d2r.checksign.lib.SignData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by magyeong-ug on 2017. 3. 29..
 */

public abstract class BaseSignRegisterFragment extends Fragment {

	private final String SIGN_FILENAME = "mySign.txt";
	protected int stepCount = 1;
	protected PublishSubject<Integer> stepSubject;
	protected OnSizeControlClick onSizeControlClick;
	protected OnSignValidationListener onSignValidationListener;
	protected CanvasSize currentSize = CanvasSize.SMALL;
	protected StringBuilder firstDatas = new StringBuilder();
	protected StringBuilder secondDatas = new StringBuilder();
	FragmentDrawBinding binding;

	public void setOnSignValidationListener(OnSignValidationListener onSignValidationListener) {
		this.onSignValidationListener = onSignValidationListener;
	}

	public void setOnSizeControlClick(OnSizeControlClick onSizeControlClick) {
		this.onSizeControlClick = onSizeControlClick;
	}

	abstract void initView();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_draw, container, false);

		stepSubject = PublishSubject.create();

		initView();

		binding.ibSizeControl.setOnClickListener(v -> {
			currentSize = currentSize == CanvasSize.SMALL ? CanvasSize.LARGE : CanvasSize.SMALL;

			if (onSizeControlClick != null) {
				onSizeControlClick.onClick(currentSize);
				stepCount = 1;
				/* 사이즈에 따른 아이콘 변경 */
				if (currentSize == CanvasSize.SMALL) {
					binding.ibSizeControl.setImageDrawable(ContextCompat.getDrawable(getActivity(),
							R.drawable.vector_drawable_icon_fullsize));

				} else {
					binding.ibSizeControl.setImageDrawable(ContextCompat.getDrawable(getActivity(),
							R.drawable.vector_drawable_icon_minimize));
				}

				/* 아이콘 색은 항상 primary */
				DrawableCompat.setTint(binding.ibSizeControl.getDrawable(),
						ContextCompat.getColor(getActivity(), R.color.colorPrimary));
			}
		});

		setOnTouchCount();

		binding.drawingCanvas.setOnDrawListener((strData) -> {

			if (stepCount == 2) {
				firstDatas.append(strData);
			} else if (stepCount == 4) {
				secondDatas.append(strData);
			}
		});

		stepSubject
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						this::setNextStepAction,
						throwable -> {
						},
						() -> {
							if (onSignValidationListener != null) {


								int similarity = -1;
								if (secondDatas.length() == 0) {
									similarity = FinoSign.validate(firstDatas.toString(), loadSavedSign());
								} else {
									similarity = FinoSign.validate(firstDatas.toString(), secondDatas.toString());
								}

								onSignValidationListener.onValidate(similarity);
								firstDatas.setLength(0);
								secondDatas.setLength(0);
							}
						});

		return binding.getRoot();
	}


	abstract void setNextStepAction(int step);

	abstract void setOnTouchCount();

	public void init() {
		binding.drawingCanvas.clear();
		stepCount = 1;
		stepSubject.onNext(stepCount);
		firstDatas.setLength(0);
		secondDatas.setLength(0);
	}

	protected String loadSavedSign() {
		return FinoSign.loadSign(getContext(), SIGN_FILENAME);
	}

	protected void saveSign() {
		FinoSign.saveSign(getContext(), SIGN_FILENAME, firstDatas.toString());
	}

	public enum CanvasSize {
		SMALL(1),
		LARGE(2);

		private int val;

		CanvasSize(int val) {
			this.val = val;
		}
	}

	public interface OnSizeControlClick {
		void onClick(CanvasSize size);
	}


	public interface OnSignValidationListener {
		void onValidate(int similarity);
	}
}
