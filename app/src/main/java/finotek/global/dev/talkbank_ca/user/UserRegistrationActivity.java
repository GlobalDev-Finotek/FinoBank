package finotek.global.dev.talkbank_ca.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding.view.RxView;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityUserRegistrationBinding;
import finotek.global.dev.talkbank_ca.user.profile.CaptureProfilePicActivity;
import finotek.global.dev.talkbank_ca.user.credit.CreditRegistrationActivity;
import rx.functions.Action1;

public class UserRegistrationActivity extends AppCompatActivity {


  private ActivityUserRegistrationBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_user_registration);

    binding.toolbar.setTitle(R.string.string_user_regi);
	  binding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
	  binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

    RxView.clicks(binding.llRegiAdditional.btnCaptureProfile)
		    .subscribe(aVoid ->
				    startActivity(new Intent(UserRegistrationActivity.this, CaptureProfilePicActivity.class)));

	  RxView.clicks(binding.llRegiAdditional.btnCaptureCreidt)
			  .subscribe(aVoid -> startActivity(new Intent(UserRegistrationActivity.this, CreditRegistrationActivity.class)));

	  RxView.clicks(binding.llRegiBasic.btnRegiSign)
			  .subscribe(aVoid -> startActivity(new Intent(UserRegistrationActivity.this, SignRegistrationActivity.class)));

	  RxView.clicks(binding.btnRegistration)
			  .subscribe(aVoid -> {
				  // startActivity(new Intent(UserRegistrationActivity.this, ));
			  });
  }
}
