package finotek.global.dev.talkbank_ca.setting;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.FragmentAbnormalTransactionBinding;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

public class AbnormalTransactionAuthFragment extends android.app.Fragment {

	private final String[] options = new String[]{
			"맥락 인증(기본)", "자필 서명", "PIN 코드", "비상 연락처",
			"인증 메일", "인증 메세지", "신용카드 OCR", "인증 사진", "음성 인식"
	};

	public static AbnormalTransactionAuthFragment newInstance(String title) {
		AbnormalTransactionAuthFragment fragment = new AbnormalTransactionAuthFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		FragmentAbnormalTransactionBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_abnormal_transaction, container, false);
		for (int i = 0; i < options.length; ++i) {
			TextView tvOpt = new TextView(getActivity());
			tvOpt.setText(options[i]);
			tvOpt.setPadding(30, 30, 30, 30);
			GridLayout.LayoutParams layoutParams = getLayoutParams(i);
			tvOpt.setLayoutParams(layoutParams);
			tvOpt.setGravity(Gravity.CENTER);

			if (i == 0) {
				tvOpt.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.info));
				tvOpt.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
			} else {
				tvOpt.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
			}

			tvOpt.setOnClickListener(new View.OnClickListener() {

				private boolean state = false;

				@Override
				public void onClick(View v) {
					if (state) {
						tvOpt.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.info));
						tvOpt.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
					} else {
						tvOpt.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
						tvOpt.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
					}

					state = !state;
				}
			});


			binding.gvOptWrapper.addView(tvOpt);
		}

		return binding.getRoot();
	}

	@NonNull
	private GridLayout.LayoutParams getLayoutParams(int idx) {
		GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
		layoutParams.setGravity(Gravity.FILL_HORIZONTAL);
		GridLayout.Spec colSpec = null;
		if (idx == 0) {
			colSpec = GridLayout.spec(0, 4, 1);
		} else if (idx % 2 == 0) {
			colSpec = GridLayout.spec(0, 2, 1);
		} else if (idx % 2 != 0) {
			colSpec = GridLayout.spec(2, 2, 1);
		}
		layoutParams.columnSpec = colSpec;
		return layoutParams;
	}
}
