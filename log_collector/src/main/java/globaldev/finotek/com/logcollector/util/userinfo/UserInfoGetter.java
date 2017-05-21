package globaldev.finotek.com.logcollector.util.userinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.R;
import globaldev.finotek.com.logcollector.app.MyApplication;

/**
 * Created by magyeong-ug on 02/05/2017.
 */

public class UserInfoGetter implements UserInfoService {

	@Inject
	MyApplication application;

	@Inject
	SharedPreferences sharedPreferences;

	@Inject
	Context context;

	@Inject
	UserInfoGetter(MyApplication application) {
		this.application = application;
	}

	@Override
	public String getUserKey() {
		return sharedPreferences.getString(context.getString(R.string.shared_prefs_push_token), "");
	}

	@Override
	public String getDeviceId() {
		return Build.PRODUCT;
	}

	@Override
	public String getDeviceModel() {
		return Build.MODEL;
	}


	@Override
	public String getPhoneNumber() {
		String phoneNumber = " ";
		try {
			TelephonyManager tMgr = (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);
			phoneNumber = tMgr.getLine1Number();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		if (TextUtils.isEmpty(phoneNumber)) {
			phoneNumber = " ";
		}

		return phoneNumber;
	}

	@Override
	public String getDeviceType() {
		return Build.BRAND;
	}
}
