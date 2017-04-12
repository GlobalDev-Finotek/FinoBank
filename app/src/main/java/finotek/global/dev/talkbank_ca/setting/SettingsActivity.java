package finotek.global.dev.talkbank_ca.setting;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

	private final int LANGUATE_SETTING = 1;
	private ActivitySettingsBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("");
		binding.toolbarTitle.setText(getString(R.string.setting_string_setting));
		binding.appbar.setOutlineProvider(null);
		binding.ibBack.setOnClickListener(v -> onBackPressed());

		Intent intent = new Intent(SettingsActivity.this, SettingDetailActivity.class);

		RxView.clicks(binding.llUserinfo)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					intent.putExtra("type", PageType.USER_INFO);
					startActivity(intent);
				});

		RxView.clicks(binding.llCa)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					intent.putExtra("type", PageType.CONTEXT_AWARE);
					startActivity(intent);
				});

		RxView.clicks(binding.llCost)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					intent.putExtra("type", PageType.COST_AUTH);
					startActivity(intent);
				});

		RxView.clicks(binding.llAbnormal)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					intent.putExtra("type", PageType.ABNORMAL_TRANSACTION);
					startActivity(intent);
				});


		RxView.clicks(binding.llLanguageSetting)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					Intent i = new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS);
					startActivityForResult(i, LANGUATE_SETTING);
					finish();
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case LANGUATE_SETTING:
				Intent i = getBaseContext().getPackageManager()
						.getLaunchIntentForPackage(getBaseContext().getPackageName());
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
				finish();
				break;
		}

	}


}
