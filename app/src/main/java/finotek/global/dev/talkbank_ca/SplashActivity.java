package finotek.global.dev.talkbank_ca;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.util.LocaleHelper;
import globaldev.finotek.com.logcollector.Finopass;
import io.realm.Realm;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

	private final String isFirstStr = "isFirst";
	private Finopass finopass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (LocaleHelper.getLanguage(this).equals("ko")) {
			DataBindingUtil.setContentView(this, R.layout.activity_splash);
		} else {
			DataBindingUtil.setContentView(this, R.layout.activity_splash_eng);
		}

		boolean isFirst = getSharedPreferences("prefs", Context.MODE_PRIVATE)
				.getBoolean(isFirstStr, true);

		if (isFirst) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.main_string_v2_create_signature_agree_no_confirm))
					.setPositiveButton(getString(R.string.dialog_button_yes), (dialog, which) -> {

						getSharedPreferences("prefs", Context.MODE_PRIVATE)
								.edit().putBoolean(isFirstStr, false).apply();

						finopass = Finopass.getInstance(SplashActivity.this);

						finopass.registerObservable()
								.subscribe(aBoolean -> moveToNextActivity());
						saveContextAuthAgreement(true);

					})
					.setNegativeButton(getString(R.string.dialog_button_no), (dialog, which) -> {
						getSharedPreferences("prefs", Context.MODE_PRIVATE)
								.edit().putBoolean(isFirstStr, false).apply();

						dialog.dismiss();
						saveContextAuthAgreement(false);
						moveToNextActivity();
					})
					.setCancelable(false)
					.show();


		} else {
			moveToNextActivity();
		}
	}

	private void saveContextAuthAgreement(boolean flag) {
		getSharedPreferences("prefs", Context.MODE_PRIVATE)
				.edit()
				.putBoolean(getString(R.string.splash_is_auth_agree), flag)
				.apply();
	}

	private void moveToNextActivity() {
		io.reactivex.Observable.interval(2, TimeUnit.SECONDS)
				.firstOrError()
				.subscribe(aLong -> {
					Intent intent;
					if (Realm.getDefaultInstance().where(User.class).count() > 0) {
						intent = new Intent(this, ChatActivity.class);
						startActivity(intent);
						finish();
					} else {
						intent = new Intent(this, MainActivity.class);
						startActivity(intent);
						finish();
					}
				});
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		finopass.onAllowedPermission(requestCode, permissions, grantResults);
	}
}

