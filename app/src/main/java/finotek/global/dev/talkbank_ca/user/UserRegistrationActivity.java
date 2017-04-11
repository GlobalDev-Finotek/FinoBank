package finotek.global.dev.talkbank_ca.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

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

	@Inject
	UserRegisterImpl presenter;
	private ActivityUserRegistrationBinding binding;

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

	  RxView.clicks(binding.btnCaptureProfile)
			  .subscribe(aVoid ->
					  startActivity(new Intent(this, CaptureProfilePicActivity.class)));

	  binding.llRegiBasic.edtPhoneNumber.setText(TelUtil.getMyPhoneNumber(this));
	  binding.llRegiBasic.edtPhoneNumber.setMode(TalkBankEditText.MODE.DISABLED);
	  
	  RxTextView.textChangeEvents(binding.llRegiBasic.edtPhoneNumber)
			  .subscribe(textViewTextChangeEvent -> {
				  String str = textViewTextChangeEvent.text().toString();

				  binding.llRegiBasic.edtPhoneNumber
						  .setErrFilter(str.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$"));

			  });

	  RxView.clicks(binding.btnCaptureCreidt)
			  .subscribe(aVoid -> {
				  Intent intent = new Intent(UserRegistrationActivity.this, CreditRegistrationActivity.class);
				  intent.putExtra("nextClass", UserRegistrationActivity.class);
				  startActivity(intent);
			  });

	  RxView.clicks(binding.llRegiBasic.btnRegiSign)
			  .subscribe(aVoid -> {
				  Intent intent = new Intent(UserRegistrationActivity.this, SignRegistrationActivity.class);
				  intent.putExtra("nextClass", UserRegistrationActivity.class);
				  startActivity(intent);
			  });

	  RxView.clicks(binding.btnPinRegistration)
			  .throttleFirst(200, TimeUnit.MILLISECONDS)
			  .subscribe(aVoid -> {
				  Intent intent = new Intent(UserRegistrationActivity.this, PinRegistrationActivity.class);
				  intent.putExtra("nextClass", UserRegistrationActivity.class);
				  startActivity(intent);
			  });

	  binding.btnRegister.setOnClickListener(v -> {
		  User user = generateUser();
		  presenter.saveUser(user);
		  startActivity(new Intent(this,
				  ChatActivity.class));
		  finish();
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

	private UserAdditionalInfo getAdditionalInfo() {
		UserAdditionalInfo userAdditionalInfo = new UserAdditionalInfo();
		userAdditionalInfo.setEmail(binding.edtEmail.getText().toString());
		userAdditionalInfo.setEmergencyPhoneNumber(binding.edtEmergencyPhoneNumber.getText().toString());
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
				.appComponent(((MyApplication) getApplication()).getAppComponent())
				.activityModule(new ActivityModule(this)).build();
	}

}
