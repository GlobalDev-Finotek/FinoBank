package finotek.global.dev.talkbank_ca.user.sign;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.FragmentDrawBinding;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by magyeong-ug on 2017. 3. 29..
 */

public class SignRegistFragment extends Fragment {

	FragmentDrawBinding binding;
	private int stepCount = 1;
	private PublishSubject<Integer> stepSubject;
	private OnSizeControlClick onSizeControlClick;
	private OnSignSaveListener onSaveListener;
	private CanvasSize currentSize = CanvasSize.SMALL;

	public void setOnSaveListener(OnSignSaveListener onSaveListener) {
		this.onSaveListener = onSaveListener;
	}

	public void setOnSizeControlClick(OnSizeControlClick onSizeControlClick) {
		this.onSizeControlClick = onSizeControlClick;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_draw, container, false);

		binding.tvInst.setText("표시된 영역 안에 서명해 주세요.(1/2)");

		stepSubject = PublishSubject.create();


		binding.drawingCanvas.setOnCanvasTouchListener(() -> {
			if (stepCount == 1 || stepCount == 3) {
				stepSubject.onNext(++stepCount);
			}
		});

		binding.ibSizeControl.setOnClickListener(v -> {
			currentSize = currentSize == CanvasSize.SMALL ? CanvasSize.LARGE : CanvasSize.SMALL;
			if (onSizeControlClick != null) {
				onSizeControlClick.onClick(currentSize);

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


		RxView.clicks(binding.ibNext)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {

					if (stepCount == 2) {
						stepSubject.onNext(++stepCount);
					}
					else if (stepCount == 4) {
						stepSubject.onCompleted();
					}
				});

		stepSubject
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						this::setNextBtnAppearance,
						throwable -> {
						},
						() -> {
							if (onSaveListener != null) onSaveListener.onSave();
						});

		return binding.getRoot();
	}

	private void setNextBtnAppearance(int step) {
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
