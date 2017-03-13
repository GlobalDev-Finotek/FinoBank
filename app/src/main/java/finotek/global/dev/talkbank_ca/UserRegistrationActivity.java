package finotek.global.dev.talkbank_ca;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.databinding.ActivityUserRegistrationBinding;
import finotek.global.dev.talkbank_ca.profile.CaptureProfilePicActivity;
import rx.functions.Action1;

public class UserRegistrationActivity extends AppCompatActivity {


  private ActivityUserRegistrationBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_user_registration);

    binding.toolbar.setTitle(R.string.string_user_regi);
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

    RxView.clicks(binding.llRegiAdditional.btnCaptureProfile)
		    .throttleFirst(200, TimeUnit.MILLISECONDS)
		    .subscribe(aVoid ->
				    startActivity(new Intent(UserRegistrationActivity.this, CaptureProfilePicActivity.class)));

  }
}
