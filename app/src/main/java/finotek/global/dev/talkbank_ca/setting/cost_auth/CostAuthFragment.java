package finotek.global.dev.talkbank_ca.setting.cost_auth;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

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

		binding.tv10.setOnClickListener(costOnClickListener);
		binding.tv50.setOnClickListener(costOnClickListener);
		binding.tv100.setOnClickListener(costOnClickListener);
		binding.tv500.setOnClickListener(costOnClickListener);

		return binding.getRoot();
	}

	View.OnClickListener costOnClickListener = new View.OnClickListener() {

		private boolean isClicked;

		@Override
		public void onClick(View v) {
			if (isClicked) {
				v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.info));
				((TextView)v).setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
			} else {
				v.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
				((TextView)v).setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_50));
			}

			isClicked = !isClicked;
		}
	};
}
