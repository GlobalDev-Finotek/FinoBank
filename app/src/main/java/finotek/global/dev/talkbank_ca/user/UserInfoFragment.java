package finotek.global.dev.talkbank_ca.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import finotek.global.dev.talkbank_ca.MainActivity;
import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.LayoutUserRegistrationBinding;
import finotek.global.dev.talkbank_ca.user.credit.CreditRegistrationActivity;
import finotek.global.dev.talkbank_ca.user.profile.CaptureProfilePicActivity;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

public class UserInfoFragment extends android.app.Fragment {

	public static UserInfoFragment newInstance(String title) {
		UserInfoFragment userInfoFragment = new UserInfoFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		userInfoFragment.setArguments(args);
		return userInfoFragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		LayoutUserRegistrationBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_user_registration, container, false);

		RxView.clicks(binding.llRegiAdditional.btnCaptureProfile)
				.subscribe(aVoid ->
						startActivity(new Intent(getActivity(), CaptureProfilePicActivity.class)));

		RxView.clicks(binding.llRegiAdditional.btnCaptureCreidt)
				.subscribe(aVoid -> startActivity(new Intent(getActivity(), CreditRegistrationActivity.class)));

		RxView.clicks(binding.llRegiBasic.btnRegiSign)
				.subscribe(aVoid -> startActivity(new Intent(getActivity(), SignRegistrationActivity.class)));

		RxView.clicks(binding.btnRegistration)
				.subscribe(aVoid -> startActivity(new Intent(getActivity(), MainActivity.class)));
		return binding.getRoot();
	}
}
