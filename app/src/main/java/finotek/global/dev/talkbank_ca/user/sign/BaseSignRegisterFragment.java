package finotek.global.dev.talkbank_ca.user.sign;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.FragmentDrawBinding;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by magyeong-ug on 2017. 3. 29..
 */

public abstract class BaseSignRegisterFragment extends Fragment {

	protected int stepCount = 1;
	protected PublishSubject<Integer> stepSubject;
	protected OnSizeControlClick onSizeControlClick;
	protected OnSignSaveListener onSaveListener;
	protected CanvasSize currentSize = CanvasSize.SMALL;
	FragmentDrawBinding binding;

	public void setOnSaveListener(OnSignSaveListener onSaveListener) {
		this.onSaveListener = onSaveListener;
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

		stepSubject
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						this::setNextStepAction,
						throwable -> {
						},
						() -> {
							if (onSaveListener != null) onSaveListener.onSave();
						});

		return binding.getRoot();
	}

	abstract void setNextStepAction(int step);

	abstract void setOnTouchCount();

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

	public interface OnSignSaveListener {
		void onSave();
	}
}
