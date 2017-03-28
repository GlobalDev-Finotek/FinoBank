package finotek.global.dev.talkbank_ca.setting.cost_auth;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.FragmentCostAuthBinding;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

public class CostAuthFragment extends android.app.Fragment {


	public static CostAuthFragment newInstance(String title) {
		CostAuthFragment fragment = new CostAuthFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		FragmentCostAuthBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cost_auth, container, false);

		return binding.getRoot();
	}
}
