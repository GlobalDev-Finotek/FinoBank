package globaldev.finotek.com.logcollector;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.app.MyApplication;
import globaldev.finotek.com.logcollector.log.LoggingHelper;

/**
 * Created by magyeong-ug on 26/04/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

	private static final String TAG = "MyFirebaseMsgService";
	@Inject
	LoggingHelper loggingHelper;

	@Override
	public void onCreate() {
		super.onCreate();

		((MyApplication) getApplication()).getAppComponent()
				.inject(this);

	}

	/**
	 * Called when message is received.
	 *
	 * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
	 */
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		Map<String, String> dataMap = remoteMessage.getData();
		loggingHelper.runLoggingService(dataMap);
	}


}
