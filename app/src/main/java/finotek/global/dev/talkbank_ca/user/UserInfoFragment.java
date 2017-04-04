package finotek.global.dev.talkbank_ca.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.LayoutUserRegistrationBinding;
import finotek.global.dev.talkbank_ca.setting.PageType;
import finotek.global.dev.talkbank_ca.setting.SettingDetailActivity;
import finotek.global.dev.talkbank_ca.user.credit.CreditRegistrationActivity;
import finotek.global.dev.talkbank_ca.user.profile.CaptureProfilePicActivity;
import finotek.global.dev.talkbank_ca.user.sign.SignRegistrationActivity;
import finotek.global.dev.talkbank_ca.util.TelUtil;
import finotek.global.dev.talkbank_ca.widget.TalkBankEditText;
import rx.functions.Action1;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

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
				.subscribe(new Action1<Void>() {
					@Override
					public void call(Void aVoid) {
						Intent intent = new Intent(getActivity(), CaptureProfilePicActivity.class);
						intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
						intent.putExtra("nextClass", SettingDetailActivity.class);
						intent.putExtra("type", PageType.USER_INFO);
						startActivity(intent);
					}
				});


		binding.llRegiBasic.edtPhoneNumber.setText(TelUtil.getMyPhoneNumber(getActivity()));
		binding.llRegiBasic.edtPhoneNumber.setMode(TalkBankEditText.MODE.DISABLED);


		RxTextView.textChangeEvents(binding.llRegiBasic.edtPhoneNumber)
				.subscribe(textViewTextChangeEvent -> {
          String str = textViewTextChangeEvent.text().toString();

					binding.llRegiBasic.edtPhoneNumber
                .setErrFilter(str.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$"));

					});

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

		binding.llRegiAdditional.btnSave.setOnClickListener(v -> getActivity().onBackPressed());

		return binding.getRoot();
	}
}
