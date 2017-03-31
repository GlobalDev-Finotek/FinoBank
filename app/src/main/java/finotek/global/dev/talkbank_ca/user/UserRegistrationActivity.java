package finotek.global.dev.talkbank_ca.user;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.databinding.ActivityUserRegistrationBinding;

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
	  getSupportActionBar().setTitle("");
	  binding.ibBack.setOnClickListener(v -> onBackPressed());

	  UserInfoFragment userInfoFragment = new UserInfoFragment();

	  FragmentTransaction transaction = getFragmentManager().beginTransaction();
	  transaction.add(R.id.fl_content, userInfoFragment);
	  transaction.commit();

	  RxView.clicks(binding.btnRegistration)
			  .throttleFirst(200, TimeUnit.MILLISECONDS)
			  .subscribe(aVoid -> startActivity(new Intent(UserRegistrationActivity.this, ChatActivity.class)));

  }
}
