package finotek.global.dev.talkbank_ca.chat.context_log;

import android.Manifest;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import globaldev.finotek.com.logcollector.model.ApplicationLog;
import globaldev.finotek.com.logcollector.model.CallHistoryLog;
import globaldev.finotek.com.logcollector.model.LocationLog;
import globaldev.finotek.com.logcollector.model.MessageLog;
import globaldev.finotek.com.logcollector.util.AesInstance;

/**
 * Created by jungwon on 7/31/2017.
 */

public class ContextLogService extends Service {

	AesInstance ai;
	//Location
	private LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}
	};
	private Location currentLocation;

	@Override
	public void onCreate() {
		super.onCreate();

		SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);

		String key = sharedPreferences
				.getString(getString(R.string.user_key, ""), "")
				.substring(0, 16);

		try {
			ai = AesInstance.getInstance(key.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Intent getContextLog = new Intent();
		getContextLog.setAction("chat.ContextLog.ContextLogService");
		getContextLog.putExtra("askType", intent.getStringExtra("askType"));
		getContextLog.putExtra("totalLog", collector(30));
		getContextLog.putParcelableArrayListExtra("smsLog", (ArrayList<? extends Parcelable>) getSms(30));
		getContextLog.putParcelableArrayListExtra("callLog", (ArrayList<? extends Parcelable>) getCall(30));
		getContextLog.putParcelableArrayListExtra("locationLog", (ArrayList<? extends Parcelable>) getLocation(30));
		getContextLog.putParcelableArrayListExtra("appLog", (ArrayList<? extends Parcelable>) getApp(30));
		getContextLog.putParcelableArrayListExtra("sampleLog", (ArrayList<? extends Parcelable>) getSampleLog());
		sendBroadcast(getContextLog);

		Log.d("seo", collector(30));
		stopSelf();
		return START_STICKY;
	}

	private Date currentTime() {
		android.icu.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		df.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

		return android.icu.util.Calendar.getInstance().getTime();
	}

	private Date dataTime(String date) {
		Date dataTime = null;

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
			dataTime = sdf.parse(date);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return dataTime;
	}

	private Date searchZone(int inputTime) {
		android.icu.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		df.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

		Calendar cal = Calendar.getInstance();
		cal.setTime(currentTime());
		cal.add(Calendar.MINUTE, -inputTime);

		Date changedTime = cal.getTime();

		return changedTime;

	}

	//SMS
	private List<MessageLog> getSms(int targetTime) {
		List<MessageLog> smsList = new ArrayList<>();

		Uri uri = Uri.parse("content://sms");
		Cursor cursor = getContentResolver().query(uri,
				new String[]{"_id", "address", "date", "body"},
				null,
				null,
				"date DESC");

		while (cursor.moveToNext()) {

			String address = cursor.getString(1);

			long timeStamp = cursor.getLong(2);
			String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", timeStamp).toString();
			String body = cursor.getString(3);
			if (dataTime(date).before(currentTime()) && dataTime(date).after(searchZone(targetTime))) {

				MessageLog messageLog = new MessageLog();
				messageLog.setLength(body.length());
				try {
					messageLog.text = ai.encText(body);
					messageLog.logTime = String.valueOf(timeStamp);
					messageLog.setTargetNumber(ai.encText(address));
				} catch (Exception e) {
					e.printStackTrace();
				}

				smsList.add(messageLog);
			}
		}
		return smsList;
	}

	// Call
	private List<CallHistoryLog> getCall(int targetTime) {
		List<CallHistoryLog> callList = new ArrayList<>();
		Uri uri = Uri.parse("content://call_log/calls");

		Cursor cursor = getContentResolver().query(uri, new String[]
						{
								CallLog.Calls.NUMBER,
								CallLog.Calls.TYPE,
								CallLog.Calls.DURATION,
								CallLog.Calls.CACHED_NAME,
								CallLog.Calls.DATE,
								CallLog.Calls._ID}
				, null, null, "date DESC");

		while (cursor.moveToNext()) {
			String string = "";

			String number = cursor.getString(0);
			String type = cursor.getString(1);
			String duration = cursor.getString(2);
			String cachedName = cursor.getString(3);

			long timeStamp = cursor.getLong(4);
			String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", timeStamp).toString();
			String id = cursor.getString(5);

			if (dataTime(date).before(currentTime()) && dataTime(date).after(searchZone(targetTime))) {
				CallHistoryLog historyLog = new CallHistoryLog();

				historyLog.duration = duration;
				historyLog.logTime = String.valueOf(timeStamp);

				try {
					historyLog.targetNumber = ai.encText(number);

					if (!TextUtils.isEmpty(cachedName)) {
						historyLog.targetName = ai.encText(cachedName);
					} else {
						historyLog.targetName = ai.encText(" ");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				callList.add(historyLog);
			}
		}

		return callList;
	}

	private List<LocationLog> getLocation(int targetTime) {
		List<LocationLog> locationList = new ArrayList<>();

		LocationManager manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

		if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

		} else {
			boolean isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (isGPSEnabled)
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this.locationListener);
			else if (isNetworkEnabled)
				manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this.locationListener);
		}

		List<String> providers = manager.getProviders(true);
		for (String prov : providers) {
			Location l = manager.getLastKnownLocation(prov);
			if (l != null) {
				currentLocation = l;
			}
		}
		if (currentLocation != null) {
			long timeStamp = currentLocation.getTime();
			String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", timeStamp).toString();

			double dLatitude = currentLocation.getLatitude();
			double dLongitude = currentLocation.getLongitude();

			if (dataTime(date).before(currentTime()) && dataTime(date).after(searchZone(targetTime))) {
				LocationLog locationLog = new LocationLog();
				locationLog.longitude = dLongitude;
				locationLog.latitude = dLatitude;
				locationLog.logTime = timeStamp;
				locationList.add(locationLog);
			} else {
				String errorMessage = "there is no location data within" + targetTime;
				System.out.print(errorMessage);
			}
		}

		return locationList;
	}

	public List<ApplicationLog> getApp(int targetTime) {
		List<ApplicationLog> appList = new ArrayList<>();
		List<UsageStats> usageStatsList = this.getUsageStatsList(getBaseContext());

		for (UsageStats u : usageStatsList) {
			PackageManager pm = getApplicationContext().getPackageManager();
			try {
				PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(u.getPackageName(), 0);

				String label = String.valueOf(foregroundAppPackageInfo.applicationInfo.loadLabel(pm));
				String startTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", u.getFirstTimeStamp()).toString();
				String endTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", u.getLastTimeStamp()).toString();

				if (dataTime(endTime).before(currentTime()) && dataTime(endTime).after(searchZone(targetTime))) {
					ApplicationLog applicationLog = new ApplicationLog();
					applicationLog.startTime = startTime;
					applicationLog.lastTimeUsed = dataTime(endTime).toString();
					applicationLog.packageName = (foregroundAppPackageInfo.packageName);
					applicationLog.setAppName(label);

					if (ai != null) {
						applicationLog.packageName = ai.encText(applicationLog.packageName);
						applicationLog.appName = ai.encText(applicationLog.appName);
					}

					applicationLog.setStartTime(String.valueOf(u.getFirstTimeStamp()));
					applicationLog.lastTimeUsed = String.valueOf(u.getLastTimeStamp());
					applicationLog.duration = u.getTotalTimeInForeground();

					if (u.getTotalTimeInForeground() > 0) {
						appList.add(applicationLog);
					}
				}
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return appList;
	}

	private List<UsageStats> getUsageStatsList(Context context) {
		UsageStatsManager usm =
				(UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		long endTime = calendar.getTimeInMillis();

		calendar.add(Calendar.HOUR, -1);

		long startTime = calendar.getTimeInMillis();

		return usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
	}

	private List<Parcelable> getSampleLog(){
		List<Parcelable> list = new ArrayList<>();
		CallHistoryLog historyLog = new CallHistoryLog();
		historyLog.duration = "65";
		historyLog.logTime = String.valueOf(System.currentTimeMillis() - (15 * 1000 * 60));
		historyLog.targetNumber = "qx5Nw6AIeT918j7HTcYOeA==";
		historyLog.targetName = "vYJVopnud5hgC7CaeB1DD7OhcOnXxMkcczPkQHxrYk8=";
		list.add(historyLog);

		LocationLog locationLog = new LocationLog();
		locationLog.logTime = System.currentTimeMillis();
		locationLog.latitude = 37.408111;
		locationLog.longitude = 126.957111;
		list.add(locationLog);

		MessageLog messageLog = new MessageLog();
		messageLog.logTime = String.valueOf(System.currentTimeMillis() - (5 * 1000 * 60));
		messageLog.targetNumber = "jddJAc97LEhy6fZiDNNFig==";
		messageLog.targetName = "vYJVopnud5hgC7CaeB1DD7OhcOnXxMkcczPkQHxrYk8=";
		messageLog.length = 63;
		list.add(messageLog);

		ApplicationLog applicationLog = new ApplicationLog();
		applicationLog.appName = "jzbjUc027qTL9O6qc//QZw==";
		applicationLog.duration = 2252.0;
		applicationLog.startTime = "1502698716626";
		list.add(applicationLog);

		return list;
	}

	public String collector(int targetTime) {
		String string = " ";

		string = String.format("SMS:%s%n Call:%s%n Location:%s%n App:%s%n",
				getSms(targetTime), getCall(targetTime), getLocation(targetTime), getApp(targetTime));

		return string;
	}


	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}