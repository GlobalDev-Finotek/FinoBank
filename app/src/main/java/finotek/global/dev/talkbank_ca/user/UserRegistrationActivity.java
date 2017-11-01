package finotek.global.dev.talkbank_ca.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.databinding.ActivityUserRegistrationBinding;
import finotek.global.dev.talkbank_ca.inject.component.DaggerUserInfoComponent;
import finotek.global.dev.talkbank_ca.inject.component.UserInfoComponent;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.model.UserAdditionalInfo;
import finotek.global.dev.talkbank_ca.user.credit.CreditRegistrationActivity;
import finotek.global.dev.talkbank_ca.user.profile.CaptureProfilePicActivity;
import finotek.global.dev.talkbank_ca.user.sign.SignRegistrationActivity;
import finotek.global.dev.talkbank_ca.util.TelUtil;
import finotek.global.dev.talkbank_ca.widget.TalkBankEditText;

public class UserRegistrationActivity extends AppCompatActivity implements UserRegisterView {

	private final int REGISTER_SIGN = 1;
	@Inject
	UserRegisterImpl presenter;
	private ActivityUserRegistrationBinding binding;
	private boolean isSigned;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_user_registration);
		getComponent().inject(this);
		presenter.attachView(this);
		binding.toolbar.setTitle(getString(R.string.registration_string_register));
		binding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
		binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("");
		binding.appbar.setOutlineProvider(null);
		binding.toolbarTitle.setText(getString(R.string.registration_string_register));
		binding.ibBack.setOnClickListener(v -> onBackPressed());

		binding.llRegiAdditional.btnSave.setVisibility(View.GONE);

		RxView.clicks(binding.llRegiAdditional.btnCaptureProfile)
				.subscribe(aVoid ->
						startActivity(new Intent(this, CaptureProfilePicActivity.class)));

		binding.llRegiBasic.edtPhoneNumber.setText(TelUtil.getMyPhoneNumber(this));
		binding.llRegiBasic.edtPhoneNumber.setMode(TalkBankEditText.MODE.DISABLED);

		RxTextView.afterTextChangeEvents(binding.llRegiBasic.edtUserName)
				.subscribe(textViewAfterTextChangeEvent -> {
					if (textViewAfterTextChangeEvent.editable().length() > 0) {
						binding.llRegiBasic.edtUserName.setMode(TalkBankEditText.MODE.FOCUS);
					}
				});

		RxView.clicks(binding.llRegiBasic.btnRegiSign)
				.subscribe(aVoid -> {
					Intent intent = new Intent(UserRegistrationActivity.this, SignRegistrationActivity.class);
					startActivityForResult(intent, REGISTER_SIGN);
				});


		RxView.clicks(binding.llRegiAdditional.btnPinRegistration)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					Intent intent = new Intent(UserRegistrationActivity.this, PinRegistrationActivity.class);
					intent.putExtra("nextClass", UserRegistrationActivity.class);
					startActivity(intent);
				});


		binding.btnRegister.setOnClickListener(v -> {
			if (checkRequiredInformationFilled()) {
				User user = generateUser();
				presenter.saveUser(user);
				startActivity(new Intent(this, ChatActivity.class));
				finish();
			} else {
				Toast.makeText(this, getString(R.string.setting_string_type_all_field), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private User generateUser() {
		User user = new User();

		user.setName(binding.llRegiBasic.edtUserName.getText().toString());
		user.setPhoneNumber(binding.llRegiBasic.edtPhoneNumber.getText().toString());

		UserAdditionalInfo additionalInfo = getAdditionalInfo();
		user.setAdditionalInfo(additionalInfo);

		return user;
	}

	private boolean checkRequiredInformationFilled() {

		boolean isNameEmpty = TextUtils.isEmpty(binding.llRegiBasic.edtUserName.getText().toString());
		boolean isPhoneNumberEmpty = TextUtils.isEmpty(binding.llRegiBasic.edtPhoneNumber.getText().toString());
		boolean isEmergencyNumberEmpty = TextUtils.isEmpty(binding.llRegiAdditional.edtEmergencyPhoneNumber.getText().toString());
		boolean isRequiredCheck = binding.cbContextAuth.isChecked();

		if (isNameEmpty) {
			binding.llRegiBasic.edtUserName.setMode(TalkBankEditText.MODE.ERROR);
		}

		if (isPhoneNumberEmpty) {
			binding.llRegiBasic.edtUserName.setMode(TalkBankEditText.MODE.ERROR);
		}

		if (isEmergencyNumberEmpty) {
			binding.llRegiAdditional.edtEmergencyPhoneNumber.setMode(TalkBankEditText.MODE.ERROR);
		}

		return !isNameEmpty && !isPhoneNumberEmpty && !isEmergencyNumberEmpty
				&& isRequiredCheck && isSigned;
	}

	private UserAdditionalInfo getAdditionalInfo() {
		UserAdditionalInfo userAdditionalInfo = new UserAdditionalInfo();
		userAdditionalInfo.setEmail(binding.llRegiAdditional.edtEmail.getText().toString());
		userAdditionalInfo.setEmergencyPhoneNumber(binding.llRegiAdditional.edtEmergencyPhoneNumber.getText().toString());
		return userAdditionalInfo;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		presenter.detachView();
	}

	private UserInfoComponent getComponent() {
		return DaggerUserInfoComponent
				.builder()
				.appComponent(((MyApplication) getApplication()).getMyAppComponent())
				.activityModule(new ActivityModule(this)).build();
	}

	@Override
	public void showLastUserData(User user) {
		binding.llRegiBasic.edtUserName.setText(user.getName());
		binding.llRegiBasic.edtPhoneNumber.setText(user.getPhoneNumber());
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REGISTER_SIGN) {
			isSigned = true;
		}

	}
}
