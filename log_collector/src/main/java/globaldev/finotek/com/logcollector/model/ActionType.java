package globaldev.finotek.com.logcollector.model;

/**
 * Created by magyeong-ug on 27/04/2017.
 * Action type defined
 */

public class ActionType {

	public static final int GATHER_DEVICE_SECURITY_LOG = 1;
	public static final int STOP_GATHER_DEVICE_SECURITY_LOG = -1;

	public static final int GATHER_LOCATION_LOG = 2;
	public static final int STOP_GATHER_LOCATION_LOG = -2;

	public static final int GATHER_CALL_LOG = 3;
	public static final int STOP_GATHER_CALL_LOG = -3;

	public static final int GATHER_MESSAGE_LOG = 4;
	public static final int STOP_GATHER_MESSAGE_LOG = -4;

	public static final int GATHER_APP_USAGE_LOG = 5;
	public static final int STOP_GATHER_APP_USAGE_LOG = -5;

	public static final int UPLOAD_LOG = 6;

}
