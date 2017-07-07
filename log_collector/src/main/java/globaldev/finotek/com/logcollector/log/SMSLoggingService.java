package globaldev.finotek.com.logcollector.log;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
	private AesInstance ai;


	public SMSLoggingService() {
		JOB_ID = ActionType.GATHER_MESSAGE_LOG;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		((FinopassApp) getApplication()).getAppComponent().inject(this);
		String key = sharedPreferences.getString(
				getBaseContext().getString(R.string.user_key), "")
				.substring(0, 16);

		try {
			ai = AesInstance.getInstance(key.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public List<MessageLog> getData(boolean isGetAllData) {

		ArrayList<MessageLog> messageLogs = new ArrayList<>();

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
				smslog.setTargetName(" ");

				long messageId = cursor.getLong(0);
				// smslog.setid(messageId);

				String address = cursor.getString(1);
				smslog.setTargetNumber(address);

				long timestamp = cursor.getLong(2);
				smslog.setTimestamp(timestamp);

				String body = " ";
				try {
					body = ai.encText(cursor.getString(3));
				} catch (Exception e) {
					e.printStackTrace();
				}
				smslog.setText(body);
				messageLogs.add(smslog);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return messageLogs;
		}

		return messageLogs;
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

	@Override
	protected void notifyJobDone(List<MessageLog> logData) {
		eventBus.publish(RxEventBus.PARSING_SMS_FINISHED, logData);
	}


}