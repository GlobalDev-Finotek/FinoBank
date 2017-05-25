package globaldev.finotek.com.logcollector.api.user;

import globaldev.finotek.com.logcollector.model.User;
import globaldev.finotek.com.logcollector.model.UserDevice;

/**
 * Created by magyeong-ug on 28/04/2017.
 */

public class UserInitParam {
	User user;
	UserDevice userDevice;

	public UserInitParam(User user, UserDevice device) {
		this.user = user;
		this.userDevice = device;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserDevice getUserDevice() {
		return userDevice;
	}

	public void setUserDevice(UserDevice userDevice) {
		this.userDevice = userDevice;
	}
}
