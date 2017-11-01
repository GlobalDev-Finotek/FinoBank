package finotek.global.dev.brazil.user.sign;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.MessageBox;
import finotek.global.dev.brazil.chat.messages.action.SignatureVerified;
import finotek.global.dev.brazil.databinding.ActivitySignRegistartionBinding;
import finotek.global.dev.brazil.user.dialogs.PrimaryDialog;
import finotek.global.dev.brazil.user.dialogs.SucceededDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class SignRegistrationActivity extends AppCompatActivity {

	private ActivitySignRegistartionBinding binding;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_registartion);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Intent intent = getIntent();

		BaseSignRegisterFragment signRegistFragment = new SignRegisterFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.fl_content, signRegistFragment);
		transaction.commit();

		signRegistFragment.setOnSignValidationListener(similarity -> {
			PrimaryDialog loadingDialog = new PrimaryDialog(SignRegistrationActivity.this);
			loadingDialog.setTitle(getString(R.string.registration_string_signature_registering));
			loadingDialog.setDescription(getString(R.string.registration_string_wait));
			loadingDialog.showWithRatio(0.50f);

			Observable.interval(1, TimeUnit.SECONDS)
					.observeOn(AndroidSchedulers.mainThread())
					.first((long) 1)
					.subscribe(i -> {

						loadingDialog.dismiss();

						SucceededDialog dialog = new SucceededDialog(SignRegistrationActivity.this);
						dialog.setDescription(getString(R.string.registration_string_register_success));
						dialog.setTitle(getString(R.string.setting_string_registered_signature));
						dialog.setButtonText(getString(R.string.setting_string_yes));
						dialog.setDoneListener(() -> {
							MessageBox.INSTANCE.add(new SignatureVerified());

							dialog.dismiss();

							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
							finish();
						});
						dialog.showWithRatio(0.50f);

					}, throwable -> {
						Log.d("Sign Register", throwable.getMessage());
					});
		});

	}

}