package finotek.global.dev.talkbank_ca.chat.ContextLog;

import android.Manifest;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
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
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jungwon on 7/31/2017.
 */

public class ContextLogService extends Service {
    @Override
    public void onCreate() {
        Log.d("seo11",collector(30));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent getContextLog = new Intent();
        getContextLog.setAction("chat.ContextLog.ContextLogService");
        getContextLog.putExtra("totalLog", collector(30));
        getContextLog.putExtra("smsLog", String.valueOf(getSms(30)));
        getContextLog.putExtra("callLog", String.valueOf(getCall(30)));
        getContextLog.putExtra("locationLog", String.valueOf(getLocation(30)));
        getContextLog.putExtra("appLog", String.valueOf(getApp(30)));
        sendBroadcast(getContextLog);


        Log.d("seo",collector(30));

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
    private List<String> getSms(int targetTime) {
        List<String> smsList = new ArrayList<>();

        Uri uri = Uri.parse("content://sms");
        Cursor cursor = getContentResolver().query(uri,
                new String[]{"_id", "address", "date", "body"},
                null,
                null,
                "date DESC");

        while (cursor.moveToNext()) {
            String string = "";

            String address = cursor.getString(1);

            long timeStamp = cursor.getLong(2);
            String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", timeStamp).toString();


            String body = cursor.getString(3);
            if (dataTime(date).before(currentTime()) && dataTime(date).after(searchZone(targetTime))) {
                string = String.format("%n address: %s%n Date: %s%n body: %s%n",
                        address, date, body);

                smsList.add(string);
            }
        }
        return smsList;
    }


    // Call
    private List<String> getCall(int targetTime) {
        List<String> callList = new ArrayList<>();
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
                string = String.format("%n number: %s%n type: %s%n duration: %s%n cachedName: %s%n date: %s%n id: %s%n",
                        number, type, duration, cachedName, date, id);

                callList.add(string);
            }
        }

        return callList;
    }


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

    private List<String> getLocation(int targetTime) {
        String string = " ";
        List<String> locationList = new ArrayList<>();

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

        long timeStamp = currentLocation.getTime();
        String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", timeStamp).toString();

        double dLatitude = currentLocation.getLatitude();
        double dLongitude = currentLocation.getLongitude();

        String latitude = String.valueOf(dLatitude);
        String longitude = String.valueOf(dLongitude);

        if (dataTime(date).before(currentTime()) && dataTime(date).after(searchZone(targetTime))) {
            string = String.format("%n date: %s%n latitude: %s%n longitude: %s%n", date, latitude, longitude);

            locationList.add(string);
        } else {
            String errorMessage = "there is no location data within" + targetTime;
            locationList.add(errorMessage);
        }

        return locationList;
    }

    public List<String> getApp(int targetTime) {
        String string = "";
        List<String> appList = new ArrayList<>();
        List<UsageStats> usageStatsList = this.getUsageStatsList(getBaseContext());

        for (UsageStats u : usageStatsList) {
            PackageManager pm = getApplicationContext().getPackageManager();
            try {
                PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(u.getPackageName(), 0);

                String label = String.valueOf(foregroundAppPackageInfo.applicationInfo.loadLabel(pm));
                String startTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", u.getFirstTimeStamp()).toString();
                String endTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", u.getLastTimeStamp()).toString();

                if(dataTime(endTime).before(currentTime()) && dataTime(endTime).after(searchZone(targetTime))) {
                    string = String.format("%n appName: %s%n startTime: %s%n endTime: %s%n",
                            label, startTime, endTime);
                    appList.add(string);
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
