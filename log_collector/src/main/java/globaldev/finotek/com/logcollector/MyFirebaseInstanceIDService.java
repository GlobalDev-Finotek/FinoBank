package globaldev.finotek.com.logcollector;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.app.MyApplication;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;

/**
 * Created by magyeong-ug on 26/04/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

	private static final String TAG = "MyFirebaseIIDService";

	@Inject
	RxEventBus eventBus;

	@Override
	public void onCreate() {
		super.onCreate();
		((MyApplication) getApplication()).getAppComponent().inject(this);
	}

	/**
	 * Called if InstanceID token is updated. This may occur if the security of
	 * the previous token had been compromised. Note that this is called when the InstanceID token
	 * is initially generated so this is where you would retrieve the token.
	 */
	// [START refresh_token]
	@Override
	public void onTokenRefresh() {
		// Get updated InstanceID token.
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();

		// If you want to send messages to this application instance or
		// manage this apps subscriptions on the server side, send the
		// Instance ID token to your app server.
		sendRegistrationToServer(refreshedToken);
	}
	// [END refresh_token]

	/**
	 * Persist token to third-party servers.
	 * <p>
	 * Modify this method to associate the user's FCM InstanceID token with any server-side account
	 * maintained by your application.
	 *
	 * @param token The new token.
	 */
	private void sendRegistrationToServer(String token) {
		eventBus.publish(RxEventBus.REGISTER_FCM, token);
	}


}
