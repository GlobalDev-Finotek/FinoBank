package finotek.global.dev.talkbank_ca.setting;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.FragmentAbnormalTransactionBinding;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

public class AbnormalTransactionAuthFragment extends android.app.Fragment {

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

		return binding.getRoot();
	}
}
