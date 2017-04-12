package finotek.global.dev.talkbank_ca.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.databinding.LayoutUserRegistrationBinding;
import finotek.global.dev.talkbank_ca.inject.component.DaggerUserInfoComponent;
import finotek.global.dev.talkbank_ca.inject.component.UserInfoComponent;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.model.UserAdditionalInfo;
import finotek.global.dev.talkbank_ca.setting.PageType;
import finotek.global.dev.talkbank_ca.setting.SettingDetailActivity;
import finotek.global.dev.talkbank_ca.user.credit.CreditRegistrationActivity;
import finotek.global.dev.talkbank_ca.user.profile.CaptureProfilePicActivity;
import finotek.global.dev.talkbank_ca.user.sign.SignRegistrationActivity;
import finotek.global.dev.talkbank_ca.util.TelUtil;
import finotek.global.dev.talkbank_ca.widget.TalkBankEditText;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

public class UserInfoFragment extends android.app.Fragment implements UserRegisterView {


	@Inject
	UserRegisterImpl presenter;

	LayoutUserRegistrationBinding binding;

	public static UserInfoFragment newInstance(String title) {
		UserInfoFragment userInfoFragment = new UserInfoFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		userInfoFragment.setArguments(args);
		return userInfoFragment;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		presenter.detachView();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.layout_user_registration, container, false);

		getComponent().inject(this);

		presenter.attachView(this);

		presenter.showLastUser();

		binding.llRegiBasic.edtPhoneNumber.setMode(TalkBankEditText.MODE.DISABLED);

		RxView.clicks(binding.llRegiAdditional.btnCaptureProfile)
				.subscribe(aVoid -> {
					Intent intent = new Intent(getActivity(), CaptureProfilePicActivity.class);
					intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("nextClass", SettingDetailActivity.class);
					intent.putExtra("type", PageType.USER_INFO);
					startActivity(intent);
				});

		binding.llRegiBasic.edtPhoneNumber.setText(TelUtil.getMyPhoneNumber(getActivity()));


		RxView.clicks(binding.llRegiAdditional.btnCaptureCreidt)
				.subscribe(aVoid -> {
					Intent intent = new Intent(getActivity(), CreditRegistrationActivity.class);
					intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("nextClass", SettingDetailActivity.class);
					intent.putExtra("type", PageType.USER_INFO);
					startActivity(intent);
				});

		RxView.clicks(binding.llRegiBasic.btnRegiSign)
				.subscribe(aVoid -> {
					Intent intent = new Intent(getActivity(), SignRegistrationActivity.class);
					intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("nextClass", SettingDetailActivity.class);
					intent.putExtra("type", PageType.USER_INFO);
					intent.putExtra("mode", SignRegistrationActivity.SignMode.TWICE);
					startActivity(intent);
				});


		RxView.clicks(binding.llRegiAdditional.btnPinRegistration)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					Intent intent = new Intent(getActivity(), PinRegistrationActivity.class);
					intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("nextClass", SettingDetailActivity.class);
					intent.putExtra("type", PageType.USER_INFO);
					startActivity(intent);
				});

		binding.llRegiAdditional.btnSave.setOnClickListener(v -> {
			User user = generateUser();
			presenter.saveUser(user);
			getActivity().onBackPressed();
		});

		return binding.getRoot();
	}

	private User generateUser() {
		User user = new User();

		user.setName(binding.llRegiBasic.edtUserName.getText().toString());
		user.setPhoneNumber(binding.llRegiBasic.edtPhoneNumber.getText().toString());

		UserAdditionalInfo additionalInfo = getAdditionalInfo();
		user.setAdditionalInfo(additionalInfo);

		return user;
	}

	private UserAdditionalInfo getAdditionalInfo() {
		UserAdditionalInfo userAdditionalInfo = new UserAdditionalInfo();
		userAdditionalInfo.setEmail(binding.llRegiAdditional.edtEmail.getText().toString());
		userAdditionalInfo.setEmergencyPhoneNumber(binding.llRegiAdditional.edtEmergencyPhoneNumber.getText().toString());
		return userAdditionalInfo;
	}

	private UserInfoComponent getComponent() {
		return DaggerUserInfoComponent
				.builder()
				.appComponent(((MyApplication) getActivity().getApplication()).getAppComponent())
				.activityModule(new ActivityModule(getActivity())).build();
	}


	@Override
	public void showLastUserData(User user) {
		binding.llRegiBasic.edtUserName.setText(user.getName());
		binding.llRegiBasic.edtPhoneNumber.setText(user.getPhoneNumber());
	}


}
