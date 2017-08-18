package globaldev.finotek.com.logcollector.log;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.CallLog;
import android.text.TextUtils;

import java.util.Calendar;
import java.util.Date;

import globaldev.finotek.com.logcollector.R;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.model.CallHistoryLog;
import globaldev.finotek.com.logcollector.util.AesInstance;

/**
 * Created by KoDeokyoon on 2017. 4. 27..
 */

public class CallLogHistoryLoggingService extends BaseLoggingService<CallHistoryLog> {


	private SharedPreferences sharedPreferences;


	public CallLogHistoryLoggingService() {
		JOB_ID = ActionType.GATHER_CALL_LOG;
	}


	private static String getNowTimeStr() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		return String.valueOf(now.getTimeInMillis());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);

	}

	@Override
	protected Class getDBClass() {
		return CallHistoryLog.class;
	}

	@Override
	public void getData(boolean isGetAllData) {


		Cursor c = null;
		String[] projection = new String[]{
				CallLog.Calls.NUMBER,
				CallLog.Calls.TYPE,
				CallLog.Calls.DURATION,
				CallLog.Calls.CACHED_NAME,
				CallLog.Calls.DATE,
				CallLog.Calls._ID
		};

		String nowStr = getNowTimeStr();
		try {

			if (isGetAllData) {

				c = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection,
						null, null, CallLog.Calls._ID + " DESC");
			} else {

				c = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection,
						CallLog.Calls.DATE + " BETWEEN ? AND ? ", new String[]{getMinus6HoursTimeStr(), nowStr}, CallLog.Calls._ID + " DESC");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		String key = sharedPreferences.getString(
				getBaseContext().getString(R.string.user_key), "")
				.substring(0, 16);


		AesInstance ai = null;
		try {
			ai = AesInstance.getInstance(key.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			assert c != null;
			if (c.getCount() != 0) {
				c.moveToFirst();

				do {
					String targetNumber = c.getString(0);


					int type = Integer.parseInt(c.getString(1)); // type, 3: missing call, 2: incoming call, 1: outgoing call
					boolean isSent = false;
					if (type == 2)
						isSent = true;

					String duration = c.getString(2);
					String targetName = c.getString(3);

					if (TextUtils.isEmpty(targetName)) {
						targetName = " ";
					}


					String timestamp = c.getString(4);

					if (ai != null) {
						targetName = ai.encText(targetName);
						targetNumber = ai.encText(targetNumber);
					}

					logData.add(new CallHistoryLog(isSent, duration, targetNumber, targetName));


				} while (c.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) c.close();
		}

	}

	private String getMinus6HoursTimeStr() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.HOUR, -1);
		return String.valueOf(c.getTimeInMillis());

	}


}
