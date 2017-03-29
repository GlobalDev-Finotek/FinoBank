package finotek.global.dev.talkbank_ca;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import kr.co.finotek.finopass.finopassvalidator.CallLogVerifier;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

	private static final int MY_PERMISSION_READ_CALL_LOG = 1;
	private final double AUTH_THRESHOLD = 0.6;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DataBindingUtil.setContentView(this, R.layout.activity_splash);

		Observable.timer(3, TimeUnit.SECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(aVoid -> {
					checkPermission();
				});
	}

	//request permissions를 이용하여 call log 의 접근권한을 획득하는 함수
	@Nullable
	private void checkPermission() {

		//현재 접근권한이 있는가
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED) {
			//사용자에게 공지가 필요한경우
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG)) {

			}
			//사용자가 필요없는 경우 강제로 권한 획득
			else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, MY_PERMISSION_READ_CALL_LOG);
			}

		}
		//권한이 있는경우 바로 호출
		else {
			Cursor c = getCallLog();
			boolean isValidUser = isValidUser(CallLogVerifier.getCallLogPassRate(c));
			moveToNextActivity(isValidUser);

		}
	}

	//권한 변경 후 호출되는 callback 함수
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			//통화기록의 권한이 변경된 경우
			case MY_PERMISSION_READ_CALL_LOG: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Cursor c = getCallLog();
					boolean isValidUser = isValidUser(CallLogVerifier.getCallLogPassRate(c));
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

	private Cursor getCallLog() {
		Log.d("aa", "Permission opened");
		String[] projection;
		projection = new String[]{
				CallLog.Calls.NUMBER,
				CallLog.Calls.TYPE,
				CallLog.Calls.DURATION,
				CallLog.Calls.CACHED_NAME,
				CallLog.Calls.DATE,
				CallLog.Calls._ID
		};

		String beforeStr = getMinus10MinTimeStr();
		String nowStr = getNowTimeStr();

		try {
				/* 지금으로부터 10분 전의 통화목록만 얻어옴 */
			return getContentResolver().query(CallLog.Calls.CONTENT_URI, projection,
					//null, null,
					CallLog.Calls.DATE + " BETWEEN ? AND ? ", new String[]{beforeStr, nowStr},
					CallLog.Calls._ID + " DESC");
		} catch (SecurityException e) {
		}
		return null;
	}

	@NonNull
	private String getNowTimeStr() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		return String.valueOf(now.getTimeInMillis());
	}


	@NonNull
	private String getMinus10MinTimeStr() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MINUTE, -10);
		return String.valueOf(c.getTimeInMillis());
	}

}
