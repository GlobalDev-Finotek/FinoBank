package finotek.global.dev.talkbank_ca;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.util.LocaleHelper;
import globaldev.finotek.com.logcollector.InitActivity;
import io.realm.Realm;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

	private static final int MY_PERMISSION_READ_CALL_LOG = 45;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (LocaleHelper.getLanguage(this).equals("ko")) {
			DataBindingUtil.setContentView(this, R.layout.activity_splash);
		} else {
			DataBindingUtil.setContentView(this, R.layout.activity_splash_eng);
		}


		ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CALL_LOG,
						android.Manifest.permission.READ_SMS,
						android.Manifest.permission.READ_PHONE_STATE},
				MY_PERMISSION_READ_CALL_LOG);

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
			io.reactivex.Observable.interval(3, TimeUnit.SECONDS)
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

	}

	//	@Override
//	protected void onAfterUserRegistered() {
//
//		io.reactivex.Observable.interval(3, TimeUnit.SECONDS)
//				.firstOrError()
//				.subscribe(aLong -> {
//					startActivity(new Intent(SplashActivity.this, ChatActivity.class));
//					finish();
//				});

//	}


}

