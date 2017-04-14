package finotek.global.dev.talkbank_ca;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.inject.component.DaggerMainComponent;
import finotek.global.dev.talkbank_ca.inject.component.MainComponent;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.util.LocaleHelper;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(LocaleHelper.getLanguage(this).equals("ko")) {
			DataBindingUtil.setContentView(this, R.layout.activity_splash);
		} else {
			DataBindingUtil.setContentView(this, R.layout.activity_splash_eng);
		}

		getComponent().inject(this);

		io.reactivex.Observable.interval(3, TimeUnit.SECONDS)
				.firstOrError()
				.subscribe(aLong -> {
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				});

	}

	public MainComponent getComponent() {
		return DaggerMainComponent.builder()
				.appComponent(((MyApplication) getApplication()).getAppComponent())
				.activityModule(new ActivityModule(this))
				.build();
	}

}
