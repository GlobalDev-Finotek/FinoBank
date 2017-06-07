package globaldev.finotek.com.logcollector.log;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import java.util.List;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.app.FinopassApp;
import globaldev.finotek.com.logcollector.model.LocationLog;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by KoDeokyoon on 2017. 5. 2..
 */

public class LocationLogService extends BaseLoggingService<LocationLog> {

	@Inject
	RxEventBus eventBus;

	private PublishSubject<List<LocationLog>> logPublishSubject = PublishSubject.create();

	private Location currentLocation = null;

	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
		}

		@Override
		public void onStatusChanged(String s, int i, Bundle bundle) {
		}

		@Override
		public void onProviderEnabled(String s) {
		}

		@Override
		public void onProviderDisabled(String s) {
		}
	};

	public LocationLogService() {

	}


	@Override
	public void onCreate() {
		super.onCreate();
		((FinopassApp) getApplication()).getAppComponent().inject(this);
	}


	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	protected void parse() {

		LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

		if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

		} else {
			boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (isGPSEnabled)
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this.mLocationListener);
			else if (isNetworkEnabled)
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this.mLocationListener);
		}

		List<String> providers = lm.getProviders(true);
		for (String prov : providers) {
			Location l = lm.getLastKnownLocation(prov);
			if (l != null) {
				currentLocation = l;
			}
		}

		if (currentLocation != null) {
			LocationLog log = new LocationLog(currentLocation.getTime(),
					currentLocation.getLatitude(), currentLocation.getLongitude());
			logData.add(log);
		}

	}

	@Override
	protected void notifyJobDone(List<LocationLog> logData) {
		eventBus.publish(RxEventBus.PARSING_LOCATION_FINISHED, logData);
	}


}
