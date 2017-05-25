package finotek.global.dev.talkbank_ca;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.inject.component.DaggerMainComponent;
import finotek.global.dev.talkbank_ca.inject.component.MainComponent;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.model.DBHelper;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.util.LocaleHelper;
import finotek.global.dev.talkbank_ca.util.SharedPrefsHelper;


public class MainActivity extends AppCompatActivity implements MainView {
	private static final int MY_PERMISSION_READ_CALL_LOG = 1;
	private static final int MY_PERMISSION_READ_CONTACTS = 2;
	private final double AUTH_THRESHOLD = 0.6;
	@Inject
	RxEventBus eventBus;
	@Inject
	SharedPrefsHelper sharedPrefsHelper;
	@Inject
	MainPresenterImpl presenter;
	@Inject
	DBHelper dbHelper;
	private TextView tvContextAuthAccuracy;
	private Button mainButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getComponent().inject(this);

		if (LocaleHelper.getLanguage(this).equals("ko")) {
			setContentView(R.layout.activity_main);
		} else {
			setContentView(R.layout.activity_main_eng);
		}

		tvContextAuthAccuracy = (TextView) findViewById(R.id.tv_context_auth_accuracy);
		mainButton = (Button) findViewById(R.id.main_button);

		presenter.attachView(this);

		dbHelper.get(User.class)
				.subscribe(users -> {
					boolean isFirst = false;

					if (users.size() == 0) {
						isFirst = true;
					}

					if (isFirst) {
						tvContextAuthAccuracy.setVisibility(View.INVISIBLE);
					} else {
						tvContextAuthAccuracy.setVisibility(View.VISIBLE);
					}

					setNextButtonText(isFirst);

				}, throwable -> {

				});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		presenter.detachView();
	}

	public MainComponent getComponent() {
		return DaggerMainComponent.builder()
				.appComponent(((MyApplication) getApplication()).getMyAppComponent())
				.activityModule(new ActivityModule(this))
				.build();
	}

	@Override
	public void setNextButtonText(boolean isFirst) {

		RxView.clicks(mainButton)
				.subscribe(aVoid ->
						presenter.moveToNextActivity(isFirst));

		if (isFirst) {
			mainButton.setText(getString(R.string.main_button_register));
		} else {
			mainButton.setText(getString(R.string.main_button_login));
		}
	}
}