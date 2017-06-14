package globaldev.finotek.com.logcollector.util.userinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.UUID;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.R;
import globaldev.finotek.com.logcollector.app.FinopassApp;

/**
 * Created by magyeong-ug on 02/05/2017.
 */

public class UserInfoGetter implements UserInfoService {

	@Inject
	FinopassApp application;

	@Inject
	SharedPreferences sharedPreferences;

	@Inject
	Context context;

	@Inject
	UserInfoGetter(FinopassApp application) {
		this.application = application;
	}

	@Override
	public String getUserKey() {
		return sharedPreferences.getString(context.getString(R.string.user_key), "");
	}

	@Override
	public String getDeviceId() {
		return UUID.randomUUID().toString();
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
