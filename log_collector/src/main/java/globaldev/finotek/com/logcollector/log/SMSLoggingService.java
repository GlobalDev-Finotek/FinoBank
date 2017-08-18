package globaldev.finotek.com.logcollector.log;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import java.util.Calendar;
import java.util.Date;

import globaldev.finotek.com.logcollector.R;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.model.MessageLog;
import globaldev.finotek.com.logcollector.util.AesInstance;

/**
 * Created by JungwonSeo on 2017-04-27.
 */

public class SMSLoggingService extends BaseLoggingService<MessageLog> {


	private SharedPreferences sharedPreferences;


	public SMSLoggingService() {
		JOB_ID = ActionType.GATHER_MESSAGE_LOG;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
	}

	@Override
	protected Class getDBClass() {
		return MessageLog.class;
	}


	@Override
	public void getData(boolean isGetAllData) {
		String key = sharedPreferences.getString(
				getBaseContext().getString(R.string.user_key), "")
				.substring(0, 16);

		AesInstance ai = null;
		try {
			ai = AesInstance.getInstance(key.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Uri uri = Uri.parse("content://sms");

		try {
			Cursor cursor;

			if (isGetAllData) {
				cursor = getContentResolver().query(uri,
						new String[]{"_id", "address", "date", "body"},
						null, null, "date DESC");
			} else {
				cursor = getContentResolver().query(uri,
						new String[]{"_id", "address", "date", "body"},
						Telephony.Sms.DATE + " BETWEEN ? AND ? ",
						new String[]{getMinus6HoursTimeStr(), getNowTimeStr()}, "date DESC");
			}

			assert cursor != null;


			while (cursor.moveToNext()) {

				MessageLog smslog = new MessageLog();


				String address = cursor.getString(1);


				long timestamp = cursor.getLong(2);


				String body = " ";
				try {
					body = cursor.getString(3);
				} catch (Exception e) {
					e.printStackTrace();
				}

				smslog.setLength(body.length());

				if (ai != null) {
					address = ai.encText(address);
					body = ai.encText(body);
				}

				smslog.setTimestamp(timestamp);
				smslog.setTargetNumber(address);
				smslog.setText(body);
				logData.add(smslog);
			}

			cursor.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getNowTimeStr() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		return String.valueOf(now.getTimeInMillis());
	}

	private String getMinus6HoursTimeStr() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.HOUR, -1);
		return String.valueOf(c.getTimeInMillis());

	}


}