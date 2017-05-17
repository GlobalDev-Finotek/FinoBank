package finotek.global.dev.talkbank_ca.inject.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.model.DBHelper;
import io.realm.Realm;

/**
 * Created by kwm on 2017. 3. 6..
 */
@Module
public class AppModule {

	private final DBHelper dbHelper;
	private MyApplication application;
	private RxEventBus eventBus;
	private Realm realm;

	public AppModule(@NonNull MyApplication application, RxEventBus eventBus,
	                 Realm realm, DBHelper dbHelper) {
		this.application = application;
		this.eventBus = eventBus;
		this.realm = realm;
		this.dbHelper = dbHelper;
	}

	@Provides
	@Singleton
	Context provideContext() {
		return this.application;
	}

	@Provides
	@Singleton
	MyApplication provideApplication() {
		return this.application;
	}

	@Provides
	@Singleton
	Realm provideRealm() {
		return realm;
	}

	@Provides
	@Singleton
	DBHelper provideDBHelper() {
		return dbHelper;
	}

	@Provides
	@Singleton
	RxEventBus provideEventBus() {
		return eventBus;
	}

	@Provides
	SharedPreferences provideSharedPreferences() {
		return this.application.getSharedPreferences("prefs", Context.MODE_PRIVATE);
	}
}
