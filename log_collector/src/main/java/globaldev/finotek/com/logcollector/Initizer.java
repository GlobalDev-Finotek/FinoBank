package globaldev.finotek.com.logcollector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;

import static globaldev.finotek.com.logcollector.InitActivity.PERMISSION_READ_LOG;

/**
 * Created by magyeong-ug on 13/06/2017.
 */

public class Initizer {

	public static void init(Activity activity) {
		ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_CALL_LOG,
						android.Manifest.permission.READ_SMS, android.Manifest.permission.SEND_SMS,
						android.Manifest.permission.BROADCAST_SMS, android.Manifest.permission.RECEIVE_SMS,
						Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission_group.SMS,
						android.Manifest.permission.READ_PHONE_STATE},
				PERMISSION_READ_LOG);
	}
}
