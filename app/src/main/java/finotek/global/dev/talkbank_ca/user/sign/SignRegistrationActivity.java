package finotek.global.dev.talkbank_ca.user.sign;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.databinding.ActivitySignRegistartionBinding;
import finotek.global.dev.talkbank_ca.user.dialogs.PrimaryDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.SucceededDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;


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