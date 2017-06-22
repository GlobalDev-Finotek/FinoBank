package globaldev.finotek.com.logcollector.model;

import com.google.gson.Gson;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by JungwonSeo on 2017-04-26.
 */
@RealmClass
public class LocationLog extends RealmObject {
	public double latitude;
	public double longitute;
	int type = ActionType.GATHER_LOCATION_LOG;
	long timestamp;
	@PrimaryKey
	private String logTime = String.valueOf(System.currentTimeMillis());

	public LocationLog() {

	}

	public LocationLog(long timestamp, double latitude, double longitude) {
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitute = longitude;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
