package globaldev.finotek.com.logcollector.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import globaldev.finotek.com.logcollector.util.eventbus.RxEventBus;

/**
 * Created by magyeong-ug on 26/04/2017.
 */
@Module
public class AppModule {

	public static final String SHARED_PREFS = "prefs";
	private final FinopassApp application;

	public AppModule(@NonNull FinopassApp application) {
		this.application = application;
	}

	@Provides
	@Singleton
	Context provideContext() {
		return this.application;
	}

	@Provides
	@Singleton
	FinopassApp provideApplication() {
		return this.application;
	}

	@Provides
	@Singleton
	SharedPreferences provideSharedPrefs() {
		return this.application.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
	}

	@Provides
	@Singleton
	RxEventBus provideEventBus() {
		return new RxEventBus();
	}


}
