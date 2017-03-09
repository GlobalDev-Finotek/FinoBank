package finotek.global.dev.talkbank_ca.util;

import android.content.SharedPreferences;

import finotek.global.dev.talkbank_ca.app.MyApplication;

/**
 * Created by magyeong-ug on 09/03/2017.
 */

public class SharedPrefUtil {


	MyApplication application;

	private SharedPreferences preferences;

	public SharedPrefUtil() {

	}

	public boolean put(String key, int value) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value);
		return editor.commit();
	}

	public boolean put(String key, boolean value) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}


}
