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

	@PrimaryKey
	protected long logTime;
	int type = ActionType.GATHER_LOCATION_LOG;

	public LocationLog() {

	}

	public LocationLog(long logTime, double latitude, double longitude) {
		this.logTime = logTime;
		this.latitude = latitude;
		this.longitute = longitude;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
