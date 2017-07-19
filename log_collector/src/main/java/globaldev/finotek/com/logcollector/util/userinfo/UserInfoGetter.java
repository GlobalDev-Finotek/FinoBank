package globaldev.finotek.com.logcollector.util.userinfo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;
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
	public String getUserName() {

		AccountManager manager = AccountManager.get(application);
		Account[] accounts = manager.getAccountsByType("com.google");
		List<String> possibleEmails = new LinkedList<String>();

		for (Account account : accounts) {
			// TODO: Check possibleEmail against an email regex or treat
			// account.name as an email address only for certain account.type
			// values.
			possibleEmails.add(account.name);
		}

		if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
			String email = possibleEmails.get(0);
			String[] parts = email.split("@");
			if (parts.length > 0 && parts[0] != null)
				return parts[0];
			else
				return " ";
		} else
			return " ";
	}


	@Override
	public String getEmail() {
		String strGmail = " ";
		Account[] accounts = AccountManager.get(application).getAccounts();

		for (Account account : accounts) {

			String possibleEmail = account.name;
			String type = account.type;

			if (type.equals("com.google")) {
				strGmail = possibleEmail;
				break;
			}
		}
		return strGmail;
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
