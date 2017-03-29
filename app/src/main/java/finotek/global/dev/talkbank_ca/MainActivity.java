package finotek.global.dev.talkbank_ca;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jakewharton.rxbinding.view.RxView;

import org.joda.time.DateTime;

import java.util.Date;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.databinding.ActivityMainBinding;
import finotek.global.dev.talkbank_ca.inject.component.DaggerMainComponent;
import finotek.global.dev.talkbank_ca.inject.component.MainComponent;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.util.SharedPrefsHelper;
import kr.co.finotek.finopass.finopassvalidator.CallLogVerifier;


public class MainActivity extends AppCompatActivity implements MainView {
	private static final int MY_PERMISSION_READ_CALL_LOG = 1;
	ActivityMainBinding binding;

	@Inject
	SharedPrefsHelper sharedPrefsHelper;

	@Inject
	MainPresenterImpl presenter;

	private final double AUTH_THRESHOLD = 0.6;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getComponent().inject(this);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		presenter.attachView(this);

		checkPermission();

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

	//request permissions를 이용하여 call log 의 접근권한을 획득하는 함수
	@Nullable
	private void checkPermission(){

		//현재 접근권한이 있는가
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)== PackageManager.PERMISSION_DENIED){
			//사용자에게 공지가 필요한경우
			if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CALL_LOG)){

			}
			//사용자가 필요없는 경우 강제로 권한 획득
			else{
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, MY_PERMISSION_READ_CALL_LOG);
			}

		}
		//권한이 있는경우 바로 호출
		else{
			Cursor c = getCallLog();
			boolean isValidUser = isValidUser(CallLogVerifier.getCallLogPassRate(c));
			setNextButtonText(isValidUser);

		}
	}

	//권한 변경 후 호출되는 callback 함수
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
		switch(requestCode){
			//통화기록의 권한이 변경된 경우
			case MY_PERMISSION_READ_CALL_LOG: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Cursor c = getCallLog();
					boolean isValidUser = isValidUser(CallLogVerifier.getCallLogPassRate(c));
					setNextButtonText(isValidUser);
				}
			}
		}
	}

	private boolean isValidUser(double authRate) {
		return authRate > AUTH_THRESHOLD;
	}

	private Cursor getCallLog() {
			Log.d("aa","Permission opened");
			String[] projection;
			projection = new String[]{
					CallLog.Calls.NUMBER,
					CallLog.Calls.TYPE,
					CallLog.Calls.DURATION,
					CallLog.Calls.CACHED_NAME,
					CallLog.Calls.DATE,
					CallLog.Calls._ID
			};

		DateTime before10Min = DateTime.now().minusMinutes(10);

		String beforeStr = before10Min.toDate().toString();
		String now = new Date().toString();

		try {
				/* 지금으로부터 10분 전의 통화목록만 얻어옴 */
				return getContentResolver().query(CallLog.Calls.CONTENT_URI, projection,
						CallLog.Calls.DATE + " BETWEEN ? AND ? ",
						new String[] {beforeStr, now},
						CallLog.Calls._ID + " DESC");
			}catch(SecurityException e){}
			return null;
	}

	@Override
	public void setNextButtonText(boolean isAuthedUser) {

		RxView.clicks(binding.mainButton)
				.subscribe(aVoid ->
						presenter.moveToNextActivity(isAuthedUser));

		if (isAuthedUser) {
			binding.mainButton.setText("사용자 등록");
		} else {
			binding.mainButton.setText("시작");
		}
	}
}
