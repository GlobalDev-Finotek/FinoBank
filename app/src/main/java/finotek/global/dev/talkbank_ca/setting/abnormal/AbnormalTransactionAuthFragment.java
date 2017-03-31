package finotek.global.dev.talkbank_ca.setting.abnormal;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.FragmentAbnormalTransactionBinding;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

public class AbnormalTransactionAuthFragment extends android.app.Fragment {

	private final String[] options = new String[]{
			"맥락 인증기본", "자필 서명", "PIN 코드", "비상 연락처",
			"인증 메일", "인증 메세지", "신용카드 OCR", "인증 사진", "음성 인식"
	};

	View.OnClickListener abnormalOnClickListener = new View.OnClickListener(){
		private boolean isClicked;

		@Override
		public void onClick(View v) {
			if(isClicked) {
				v.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.info));
				((TextView) v).setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
			} else {
				v.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
				((TextView) v).setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_50));
			}
			isClicked = !isClicked;
		}
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

		RxView.clicks(binding.btnSave)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> getActivity().onBackPressed());

		binding.tvContextAu.setOnClickListener(abnormalOnClickListener);
		binding.tvPsignature.setOnClickListener(abnormalOnClickListener);
		binding.tvPinCode.setOnClickListener(abnormalOnClickListener);
		binding.tvEmergencyContact.setOnClickListener(abnormalOnClickListener);
		binding.tvAuMail.setOnClickListener(abnormalOnClickListener);
		binding.tvAuMessage.setOnClickListener(abnormalOnClickListener);
		binding.tvCreditCardOcr.setOnClickListener(abnormalOnClickListener);
		binding.tvAuPicture.setOnClickListener(abnormalOnClickListener);
		binding.tvVoice.setOnClickListener(abnormalOnClickListener);

		return binding.getRoot();
	}

	@NonNull
	private TextView generateTvOpt(String option) {
		TextView tvOpt = new TextView(getActivity());
		tvOpt.setText(option);
		tvOpt.setPadding(40, 40, 40, 40);
		tvOpt.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.cell_shape));

		tvOpt.setOnClickListener(new View.OnClickListener() {

			private boolean state = false;

			@Override
			public void onClick(View v) {
				if (state) {
					DrawableCompat.setTint(tvOpt.getBackground(), ContextCompat.getColor(getActivity(), R.color.info));
					tvOpt.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
				} else {
					DrawableCompat.setTint(tvOpt.getBackground(), ContextCompat.getColor(getActivity(), R.color.white));
					tvOpt.setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_50));
				}

				state = !state;
			}
		});

		if (option.equals(options[0])) {
			DrawableCompat.setTint(tvOpt.getBackground(), ContextCompat.getColor(getActivity(), R.color.info));
			tvOpt.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
		} else {
			DrawableCompat.setTint(tvOpt.getBackground(), ContextCompat.getColor(getActivity(), R.color.white));
			tvOpt.setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_50));
		}

		return tvOpt;
	}

	private TableRow getTableRow(int idx) {
		TableRow tableRow = new TableRow(getActivity());
		tableRow.setGravity(Gravity.CENTER);

		if (idx == 0) {
			tableRow.addView(generateTvOpt(options[0]));
		} else if (idx > 0 && idx < options.length - 2) {

			int i = idx;
			while (i != idx + 2) {
				tableRow.addView(generateTvOpt(options[i++]));
			}

		}

		return tableRow;
	}


}
