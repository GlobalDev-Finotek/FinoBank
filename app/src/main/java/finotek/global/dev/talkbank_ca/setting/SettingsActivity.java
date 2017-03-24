package finotek.global.dev.talkbank_ca.setting;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivitySettingsBinding;
import finotek.global.dev.talkbank_ca.user.UserRegistrationActivity;

public class SettingsActivity extends AppCompatActivity {

	private ActivitySettingsBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("설정");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

		RxView.clicks(binding.llUserinfo)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					startActivity(new Intent(SettingsActivity.this, UserRegistrationActivity.class));
					finish();
				});

		RxView.clicks(binding.llCa)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					// TODO 맥락 인증 액티비티 생성 및 연결
				});

		RxView.clicks(binding.llCost)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					// TODO 금액 별 인증 액티비티 생성 및 연결
				});

		RxView.clicks(binding.llAbnormal)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					// TODO 이상 거래 시 인증 및 연결
				});

	}
}
