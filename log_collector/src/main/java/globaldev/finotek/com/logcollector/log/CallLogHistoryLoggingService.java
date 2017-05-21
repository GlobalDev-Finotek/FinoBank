package globaldev.finotek.com.logcollector.log;

import android.database.Cursor;
import android.provider.CallLog;
import android.text.TextUtils;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.app.MyApplication;
import globaldev.finotek.com.logcollector.model.CallHistoryLog;
import globaldev.finotek.com.logcollector.util.LogUtil;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;

/**
 * Created by KoDeokyoon on 2017. 4. 27..
 */

public class CallLogHistoryLoggingService extends BaseLoggingService<CallHistoryLog> {

	@Inject
	RxEventBus eventBus;

	private long startTime;

	CallLogHistoryLoggingService() {

	}

	CallLogHistoryLoggingService(long startTime) {
		this.startTime = startTime;
	}


	private static String getNowTimeStr() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		return String.valueOf(now.getTimeInMillis());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		((MyApplication) getApplication()).getAppComponent().inject(this);
	}

	@Override
	protected void parse() {

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
			c = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection,
					CallLog.Calls.DATE + " BETWEEN ? AND ? ", new String[]{getMinus6HoursTimeStr(), nowStr}, CallLog.Calls._ID + " DESC");

		} catch (SecurityException e) {
			e.printStackTrace();
		}


		try {
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

					String logTime = c.getString(4);

					logData.add(new CallHistoryLog(logTime, isSent, duration, targetNumber, targetName));


				} while (c.moveToNext());
			}
		} catch (SecurityException e) {
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

	@Override
	protected void notifyJobDone(List<CallHistoryLog> logData) {
		eventBus.publish(RxEventBus.PARSING_CALL_FINISHED, logData);
	}

}
