package globaldev.finotek.com.logcollector.log;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.R;
import globaldev.finotek.com.logcollector.app.FinopassApp;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.model.MessageLog;
import globaldev.finotek.com.logcollector.util.AesInstance;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;

/**
 * Created by JungwonSeo on 2017-04-27.
 */

public class SMSLoggingService extends BaseLoggingService<MessageLog> {


	@Inject
	SharedPreferences sharedPreferences;

	@Inject
	RxEventBus eventBus;


	public SMSLoggingService() {
		JOB_ID = ActionType.GATHER_MESSAGE_LOG;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		((FinopassApp) getApplication()).getAppComponent().inject(this);

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

			int count = 0;
			while (cursor.moveToNext()) {

				MessageLog smslog = new MessageLog();

				long messageId = cursor.getLong(0);
				// smslog.setid(messageId);

				String address = cursor.getString(1);


				long timestamp = cursor.getLong(2);


				String body = " ";
				try {
					body = cursor.getString(3);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (ai != null) {
					address = ai.encText(address);
					body = ai.encText(body);
				}

				smslog.setTimestamp(timestamp);
				smslog.setTargetNumber(address);
				smslog.setText(body);
				logData.add(smslog);
			}
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