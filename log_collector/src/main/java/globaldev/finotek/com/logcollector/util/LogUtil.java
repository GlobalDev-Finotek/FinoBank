package globaldev.finotek.com.logcollector.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by magyeong-ug on 19/05/2017.
 */

public class LogUtil {

	public static final String TAG = LogUtil.class.getCanonicalName();
	private static final String processId = Integer.toString(android.os.Process
			.myPid());

	public static StringBuilder getLog() {

		StringBuilder builder = new StringBuilder();

		try {
			String[] command = new String[]{"logcat", "-d", TAG, "*:S"};

			Process process = Runtime.getRuntime().exec(command);

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				builder.append(line);

			}
		} catch (IOException ex) {
			Log.e(TAG, "getLog failed", ex);
		}

		return builder;
	}

	public static void write(String content) {
		Log.d(TAG, content);
	}
}
