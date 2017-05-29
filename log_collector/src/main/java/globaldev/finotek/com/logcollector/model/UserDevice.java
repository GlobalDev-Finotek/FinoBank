package globaldev.finotek.com.logcollector.model;

/**
 * Created by magyeong-ug on 27/04/2017.
 */

public class UserDevice {
	private String deviceKey;
	private String type;
	private String model;
	private boolean isInUse = false;
	private String pushToken;

	public UserDevice(String key, String type, String model, boolean isInUse, String pushToken) {
		this.deviceKey = key;
		this.type = type;
		this.model = model;
		this.isInUse = isInUse;
		this.pushToken = pushToken;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public boolean isInUse() {
		return isInUse;
	}

	public void setInUse(boolean inUse) {
		isInUse = inUse;
	}

	public String getPushToken() {
		return pushToken;
	}

	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}
}
