package globaldev.finotek.com.logcollector.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;

/**
 * Created by magyeong-ug on 26/04/2017.
 */
public class AppModule {

	public static final String SHARED_PREFS = "prefs";
	private final Application application;

	public AppModule(@NonNull Application application) {
		this.application = application;
	}

	Context provideContext() {
		return this.application;
	}

	Application provideApplication() {
		return this.application;
	}

	SharedPreferences provideSharedPrefs() {
		return this.application.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
	}

	RxEventBus provideEventBus() {
		return new RxEventBus();
	}


}
