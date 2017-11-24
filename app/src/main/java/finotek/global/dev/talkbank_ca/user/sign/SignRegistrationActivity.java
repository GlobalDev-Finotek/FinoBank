package finotek.global.dev.talkbank_ca.user.sign;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivitySignRegistartionBinding;
import finotek.global.dev.talkbank_ca.user.dialogs.WarningDialog;
import globaldev.finotek.com.finosign.view.SignRegisterFragment;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class SignRegistrationActivity extends AppCompatActivity implements SignRegisterFragment.OnSignRegisterListener {

	private ActivitySignRegistartionBinding binding;
	private SignRegisterFragment signRegisterFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_registartion);
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("");
		binding.toolbarTitle.setText(getString(R.string.registration_string_signnature));
		binding.appbar.setOutlineProvider(null);
		binding.ibBack.setOnClickListener(v -> onBackPressed());

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		signRegisterFragment = new SignRegisterFragment();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.fl_content, signRegisterFragment);
		transaction.commit();

	}



	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		int orientation = newConfig.orientation;

		switch (orientation) {

			case Configuration.ORIENTATION_LANDSCAPE:
				binding.appbar.setVisibility(View.GONE);
				break;

			case Configuration.ORIENTATION_PORTRAIT:
				binding.appbar.setVisibility(View.VISIBLE);
				break;

		}
	}

	@Override
	public void onRegisterDone(boolean isDone) {
		if (isDone) {
			finish();
		} else {
			WarningDialog warningDialog = new WarningDialog(SignRegistrationActivity.this);
			warningDialog.setTitle(getString(R.string.setting_string_click_re_try_button));
			warningDialog.setButtonText(getString(R.string.setting_string_re_try));
			warningDialog.setDoneListener(() -> Observable.interval(1200, TimeUnit.MILLISECONDS)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(aLong -> warningDialog.dismiss()));

			warningDialog.show();
		}
	}

	public static class SignMode {
		public static int ONCE = 1;
		public static int TWICE = 2;
	}
}