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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.api.log.ApiServiceImpl;
import globaldev.finotek.com.logcollector.app.FinopassApp;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.model.LocationLog;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by KoDeokyoon on 2017. 5. 2..
 */

public class LocationLogService extends BaseLoggingService<LocationLog> {

	@Inject
	ApiServiceImpl logService;

	@Inject
	RxEventBus eventBus;

	private PublishSubject<List<LocationLog>> logPublishSubject = PublishSubject.create();

	private Location currentLocation = null;



	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			currentLocation = location;
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
		JOB_ID = ActionType.GATHER_LOCATION_LOG;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		((FinopassApp) getApplication()).getAppComponent().inject(this);
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	public List<LocationLog> getData(boolean isGetAllData) {

		ArrayList<LocationLog> locationLogs = new ArrayList<>();

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
			LocationLog l = new LocationLog(currentLocation.getTime(),
					currentLocation.getLatitude(), currentLocation.getLongitude());
			locationLogs.add(l);
		}

		return locationLogs;
	}



	/*
	@Override
	protected void uploadLogs(String userKey) {

		logService.updateLocationLog(userKey, logData)
				.retry(3)
				.subscribe(new Consumer() {
					@Override
					public void accept(Object o) throws Exception {
						System.out.print(o);
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						saveLogs();
						System.out.print(throwable);
					}
				}, new Action() {
					@Override
					public void run() throws Exception {
						clearDB(LocationLog.class);
					}
				});
	}
	*/


}
