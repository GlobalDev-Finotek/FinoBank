package globaldev.finotek.com.logcollector.log;

import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Environment;
import android.provider.Settings;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.app.MyApplication;
import globaldev.finotek.com.logcollector.model.DeviceSecurityLevel;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;

public class SecurityLevelLoggingService extends BaseLoggingService<DeviceSecurityLevel> {
	private final static String PASSWORD_TYPE_KEY = "lockscreen.password_type";

	@Inject
	RxEventBus eventBus;

	SecurityLevelLoggingService() {

	}


	@Override
	public void onCreate() {
		super.onCreate();
		((MyApplication) getApplication()).getAppComponent().inject(this);
	}

	@Override
	protected void parse() {


		DeviceSecurityLevel log = new DeviceSecurityLevel();
		// TODO getLockType 오류
		// assign to @dh
		// log.setUserKey(sharedPreferences.getString(context.getString(R.string.shared_prefs_push_token), ""));
		log.lockType = "";
		log.isLocked = !log.lockType.equals("none_or_slide");

		logData.add(log);

	}

	@Override
	protected void notifyJobDone(List<DeviceSecurityLevel> logData) {
		eventBus.publish(RxEventBus.PARSING_SECURITY_FINISHED, logData);
	}

	private String getLockType(Context context) {
		ContentResolver contentResolver = context.getContentResolver();

		long mode = android.provider.Settings.Secure.getLong(contentResolver, PASSWORD_TYPE_KEY,
				DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);

		if (mode == DevicePolicyManager.PASSWORD_QUALITY_SOMETHING) {
			if (android.provider.Settings.Secure.getInt(contentResolver, Settings.Secure.LOCK_PATTERN_ENABLED, 0) == 1) {
				return "pattern";
			} else return "none_or_slide";
		} else if (mode == DevicePolicyManager.PASSWORD_QUALITY_BIOMETRIC_WEAK) {
			String dataDirPath = Environment.getDataDirectory().getAbsolutePath();
			if (nonEmptyFileExists(dataDirPath + "/system/gesture.key")) {
				return "face_with_pattern";
			} else if (nonEmptyFileExists(dataDirPath + "/system/password.key")) {
				return "face_with_pin";
			} else return "face_with_something_else";
		} else if (mode == DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC) {
			return "password_alphanumeric";
		} else if (mode == DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC) {
			return "password_alphabetic";
		} else if (mode == DevicePolicyManager.PASSWORD_QUALITY_NUMERIC) {
			return "pin";
		} else return "something_else";
	}

	private boolean nonEmptyFileExists(String filename) {
		File file = new File(filename);
		return file.exists() && file.length() > 0;
	}


}
