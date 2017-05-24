package finotek.global.dev.talkbank_ca;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.util.LocaleHelper;
import globaldev.finotek.com.logcollector.InitActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends InitActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (LocaleHelper.getLanguage(this).equals("ko")) {
			DataBindingUtil.setContentView(this, R.layout.activity_splash);
		} else {
			DataBindingUtil.setContentView(this, R.layout.activity_splash_eng);
		}

	}

	@Override
	protected void onAfterUserRegistered() {

		io.reactivex.Observable.interval(3, TimeUnit.SECONDS)
				.firstOrError()
				.subscribe(aLong -> {
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				});

	}


}
