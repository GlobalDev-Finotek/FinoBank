package finotek.global.dev.talkbank_ca.setting.abnormal;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.FragmentAbnormalTransactionBinding;

public class AbnormalTransactionAuthFragment extends android.app.Fragment {
	View.OnClickListener abnormalOnClickListener = new View.OnClickListener() {
		private boolean isClicked = false;

		@Override
		public void onClick(View v) {
			if (isClicked) {
				v.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
				((TextView) v).setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_50));
			} else {
				v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.info));
				((TextView) v).setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
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
}
