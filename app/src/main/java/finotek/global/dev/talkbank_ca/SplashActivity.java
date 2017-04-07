package finotek.global.dev.talkbank_ca;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.base.mvp.event.AccuracyMeasureEvent;
import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.inject.component.DaggerMainComponent;
import finotek.global.dev.talkbank_ca.inject.component.MainComponent;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import kr.co.finotek.finopass.finopassvalidator.CallLogVerifier;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

	private static final int MY_PERMISSION_READ_CALL_LOG = 1;
	private final double AUTH_THRESHOLD = 0.6;

	@Inject
	RxEventBus eventBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DataBindingUtil.setContentView(this, R.layout.activity_splash);

		getComponent().inject(this);


		checkPermission();

	}

	//request permissions를 이용하여 call log 의 접근권한을 획득하는 함수
	@Nullable
	private void checkPermission() {

		//현재 접근권한이 있는가
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
			//사용자에게 공지가 필요한경우
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG)) {

			}
			//사용자가 필요없는 경우 강제로 권한 획득
			else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE},
						MY_PERMISSION_READ_CALL_LOG);
			}

		}
		//권한이 있는경우 바로 호출
		else {
			double accuracy = CallLogVerifier.getCallLogPassRate(this);
			boolean isValidUser = isValidUser(accuracy);

			eventBus.sendEvent(new AccuracyMeasureEvent(accuracy));
			moveToNextActivity(isValidUser);
			finish();
		}
	}

	//권한 변경 후 호출되는 callback 함수
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			//통화기록의 권한이 변경된 경우
			case MY_PERMISSION_READ_CALL_LOG: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					double accuracy = CallLogVerifier.getCallLogPassRate(this);
					boolean isValidUser = isValidUser(accuracy);
					eventBus.sendEvent(new AccuracyMeasureEvent(accuracy));
					moveToNextActivity(isValidUser);
				}
			}
		}
	}

	private void moveToNextActivity(boolean isValidUser) {

		Intent intent;
		if (!isValidUser) {
			intent = new Intent(SplashActivity.this, MainActivity.class);
		} else {
			intent = new Intent(SplashActivity.this, ChatActivity.class);
		}


		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

	}

	private boolean isValidUser(double authRate) {
		return authRate > AUTH_THRESHOLD;
	}



	public MainComponent getComponent() {
		return DaggerMainComponent.builder()
				.appComponent(((MyApplication) getApplication()).getAppComponent())
				.activityModule(new ActivityModule(this))
				.build();
	}

}
