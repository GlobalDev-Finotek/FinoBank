
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

import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.base.mvp.event.AccuracyMeasureEvent;
import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.databinding.ActivityMainBinding;
import finotek.global.dev.talkbank_ca.inject.component.DaggerMainComponent;
import finotek.global.dev.talkbank_ca.inject.component.MainComponent;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.model.DBHelper;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.util.SharedPrefsHelper;
import io.realm.RealmResults;
import kr.co.finotek.finopass.finopassvalidator.CallLogVerifier;
import rx.functions.Action1;


public class MainActivity extends AppCompatActivity implements MainView {


	private static final int MY_PERMISSION_READ_CALL_LOG = 1;
	private final double AUTH_THRESHOLD = 0.6;

	ActivityMainBinding binding;

	@Inject
	RxEventBus eventBus;

	@Inject
	SharedPrefsHelper sharedPrefsHelper;

	@Inject
	MainPresenterImpl presenter;

	@Inject
	DBHelper dbHelper;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getComponent().inject(this);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		presenter.attachView(this);
		checkPermission();

		dbHelper.get(User.class)
				.subscribe(new Action1<RealmResults<User>>() {
					@Override
					public void call(RealmResults<User> users) {
						boolean isFirst = false;

						if (users.size() == 0) {
							isFirst = true;
						}

						setNextButtonText(isFirst);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {

					}
				});


	}

	private void moveToNextActivity(boolean isValidUser) {

		Intent intent;
		if (isValidUser) {
			intent = new Intent(this, ChatActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			finish();
		}

	}

	private boolean isValidUser(double authRate) {
		return authRate > AUTH_THRESHOLD;
	}

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

			String inst = getString(R.string.dialog_chat_verified_context_data).replace("%d", String.valueOf((int) (accuracy * 100)));
			setInstructionText(inst, isValidUser);

			eventBus.sendEvent(new AccuracyMeasureEvent(accuracy));
			moveToNextActivity(isValidUser);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		presenter.detachView();
	}

	public MainComponent getComponent() {
		return DaggerMainComponent.builder()
				.appComponent(((MyApplication) getApplication()).getAppComponent())
				.activityModule(new ActivityModule(this))
				.build();
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

					String inst = getString(R.string.dialog_chat_verified_context_data).replace("%d", String.valueOf((int) (accuracy * 100)));
					setInstructionText(inst, isValidUser);

					eventBus.sendEvent(new AccuracyMeasureEvent(accuracy));
					moveToNextActivity(isValidUser);
				}
			}
		}
	}

	private void setInstructionText(String baseInstruction, boolean isValidUser) {


		if (isValidUser) {
			baseInstruction += " " + getString(R.string.main_string_authentication_succeed);
		} else {
			baseInstruction += " " + getString(R.string.main_string_authentication_required);
		}

		binding.tvContextAuthAccuracy.setText(baseInstruction);
	}

	@Override
	public void setNextButtonText(boolean isFirst) {

		RxView.clicks(binding.mainButton)
				.subscribe(aVoid ->
						presenter.moveToNextActivity(isFirst));

		if (isFirst) {
			binding.mainButton.setText("사용자 등록");
		} else {
			binding.mainButton.setText("로그인");
		}
	}
}