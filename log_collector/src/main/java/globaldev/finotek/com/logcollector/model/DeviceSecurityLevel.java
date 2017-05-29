package globaldev.finotek.com.logcollector.model;

import com.google.gson.Gson;

import io.realm.RealmObject;

/**
 * Created by JungwonSeo on 2017-04-26.
 */

public class DeviceSecurityLevel extends RealmObject {
	public boolean isLocked;
	public String lockType;
	protected String logTime;
	int type = ActionType.GATHER_DEVICE_SECURITY_LOG;

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
