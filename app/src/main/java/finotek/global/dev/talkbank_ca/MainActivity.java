package finotek.global.dev.talkbank_ca;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.base.mvp.event.AccuracyMeasureEvent;
import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.databinding.ActivityMainBinding;
import finotek.global.dev.talkbank_ca.inject.component.DaggerMainComponent;
import finotek.global.dev.talkbank_ca.inject.component.MainComponent;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.util.SharedPrefsHelper;


public class MainActivity extends AppCompatActivity implements MainView {
	ActivityMainBinding binding;

	@Inject
	SharedPrefsHelper sharedPrefsHelper;

	@Inject
	MainPresenterImpl presenter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getComponent().inject(this);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		presenter.attachView(this);

		boolean isFirst = sharedPrefsHelper.get("isFirst", true);
		setNextButtonText(isFirst);

		RxEventBus.getInstance().getObservable()
				.subscribe(iEvent -> {
					if (iEvent instanceof AccuracyMeasureEvent) {
						double accuracy = ((AccuracyMeasureEvent) iEvent).getAccuracy();
						String inst = getString(R.string.dialog_chat_verified_context_data).replace("%d", String.valueOf((int) (accuracy * 100)));
						binding.tvContextAuthAccuracy.setText(inst);
					}
				});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		presenter.detachView();
	}

	public MainComponent getComponent() {
		return DaggerMainComponent.builder()
				.appComponent(((MyApplication)getApplication()).getAppComponent())
				.activityModule(new ActivityModule(this))
				.build();
	}

	@Override
	public void setNextButtonText(boolean isFirst) {

		RxView.clicks(binding.mainButton)
				.subscribe(aVoid ->
						presenter.moveToNextActivity(isFirst));

		if (isFirst) {
			binding.mainButton.setText("사용자 등록");
			sharedPrefsHelper.put("isFirst", false);
		} else {
			binding.mainButton.setText("로그인");
		}
	}
}
