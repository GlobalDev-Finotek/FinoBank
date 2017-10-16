package finotek.global.dev.talkbank_ca.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityPincodeChangeBinding;

public class PinChangeActivity extends AppCompatActivity {
	ActivityPincodeChangeBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_pincode_change);

		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("");
		binding.toolbarTitle.setText(getString(R.string.setting_string_pin_code));
		binding.appbar.setOutlineProvider(null);
		binding.ibBack.setOnClickListener(v -> onBackPressed());

		RxView.clicks(binding.pinCode)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					Intent intent = new Intent(this, PinRegistrationActivity.class);
					intent.putExtra("title", getString(R.string.setting_string_pin_code));
					startActivity(intent);
				});

		RxView.clicks(binding.checkPinCode)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					Intent intent = new Intent(this, PinRegistrationActivity.class);
					intent.putExtra("title", getString(R.string.setting_string_pin_code_will_change));
					startActivity(intent);
				});

		RxView.clicks(binding.confirmPinCode)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					Intent intent = new Intent(this, PinRegistrationActivity.class);
					intent.putExtra("title", getString(R.string.setting_string_pin_code_will_change_confirm));
					startActivity(intent);
				});

		RxView.clicks(binding.btnSave)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					finish();
				});
	}
}
