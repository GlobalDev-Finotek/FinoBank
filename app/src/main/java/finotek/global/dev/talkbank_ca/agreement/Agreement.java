package finotek.global.dev.talkbank_ca.agreement;

import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityAgreementBinding;

/**
 * Created by JungwonSeo on 2017-03-23.
 */

public class Agreement extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedlnstanceState) {
		ActivityAgreementBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_agreement, container, false);
		View view = binding.getRoot();
		// check one -> click all
		binding.radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (binding.radioButton.isChecked()) {
				binding.radioButton2.setChecked(true);
				binding.radioButton3.setChecked(true);
				binding.radioButton4.setChecked(true);
				binding.radioButton5.setChecked(true);
			}

			// show_up signature button
			if (binding.radioButton.isChecked() && binding.radioButton6.isChecked()) {
				binding.button.setVisibility(View.VISIBLE);
			}
		});

		//individual check makes check_all button to be true
		binding.radioButton5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (binding.radioButton2.isChecked() && binding.radioButton3.isChecked() && binding.radioButton4.isChecked() && binding.radioButton5.isChecked()) {

					binding.radioButton.setChecked(true);
				}

			}
		});
		binding.radioButton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (binding.radioButton2.isChecked() && binding.radioButton3.isChecked() && binding.radioButton4.isChecked() && binding.radioButton5.isChecked()) {
					binding.radioButton.setChecked(true);
				}

			}
		});

		binding.radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (binding.radioButton2.isChecked() && binding.radioButton3.isChecked() && binding.radioButton4.isChecked() && binding.radioButton5.isChecked()) {
					binding.radioButton.setChecked(true);
				}

			}
		});

		binding.radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (binding.radioButton2.isChecked() && binding.radioButton3.isChecked() && binding.radioButton4.isChecked() && binding.radioButton5.isChecked()) {
					binding.radioButton.setChecked(true);
				}

			}
		});

		binding.radioButton6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (binding.radioButton6.isChecked()) {
					binding.radioButton6.setChecked(true);
					binding.radioButton7.setChecked(true);
				}

				if (binding.radioButton6.isChecked() && binding.radioButton.isChecked()) {
					binding.button.setVisibility(View.VISIBLE);
				}
			}
		});

		binding.radioButton7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (binding.radioButton7.isChecked()) {

					binding.radioButton6.setChecked(true);
				}
			}
		});

		binding.button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent move = new Intent(getActivity(), eSignature.class);

				startActivity(move);
			}
		});


		binding.agreement1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// open contentAgreement page with sending "num" "[number]"
				Intent move = new Intent(getActivity(), ContentAgreement.class);
				move.putExtra("num", 1);


				startActivity(move);
			}
		});

		binding.agreement2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {


				Intent move = new Intent(getActivity().getApplicationContext(), ContentAgreement.class);
				move.putExtra("num", 2);

				startActivity(move);
			}
		});

		binding.agreement3.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {


				Intent move = new Intent(getActivity().getApplicationContext(), ContentAgreement.class);
				move.putExtra("num", 3);

				startActivity(move);
			}
		});

		binding.agreement4.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent move = new Intent(getActivity(), ContentAgreement.class);
				move.putExtra("num", 4);


				startActivity(move);
			}
		});

		binding.agreement5.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {


				Intent move = new Intent(getActivity().getApplicationContext(), ContentAgreement.class);
				move.putExtra("num", 5);

				startActivity(move);
			}
		});


		return view;
	}

}
