package finotek.global.dev.brazil.inject.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import finotek.global.dev.brazil.app.MyApplication;
import finotek.global.dev.brazil.base.mvp.event.RxEventBus;
import finotek.global.dev.brazil.model.DBHelper;

/**
 * Created by kwm on 2017. 3. 6..
 */
@Module
public class AppModule {

	private final DBHelper dbHelper;
	private MyApplication application;
	private RxEventBus eventBus;

	public AppModule(@NonNull MyApplication application, RxEventBus eventBus, DBHelper dbHelper) {
		this.application = application;
		this.eventBus = eventBus;
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
