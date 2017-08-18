package globaldev.finotek.com.logcollector.util.userinfo;

/**
 * Created by magyeong-ug on 02/05/2017.
 */

public interface UserInfoGetter {

	String getUserName();

	String getEmail();

	String getUserKey();

	String getDeviceId();

	String getDeviceModel();

	String getPhoneNumber();

	String getDeviceType();
}
