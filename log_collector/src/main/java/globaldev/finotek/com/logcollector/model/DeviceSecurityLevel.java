package globaldev.finotek.com.logcollector.model;

import com.google.gson.Gson;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by JungwonSeo on 2017-04-26.
 */
@RealmClass
public class DeviceSecurityLevel extends RealmObject {
	public boolean isLocked;
	public String lockType;
	int type = ActionType.GATHER_DEVICE_SECURITY_LOG;
	@PrimaryKey
	private String logTime = String.valueOf(System.currentTimeMillis());

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
