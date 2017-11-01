package finotek.global.dev.brazil.app;

import android.app.Application;
import android.content.Context;

import finotek.global.dev.brazil.base.mvp.event.RxEventBus;
import finotek.global.dev.brazil.inject.component.AppComponent;
import finotek.global.dev.brazil.inject.component.DaggerAppComponent;
import finotek.global.dev.brazil.inject.module.AppModule;
import finotek.global.dev.brazil.model.DBHelper;
import globaldev.finotek.com.logcollector.db.FinopassRealmModule;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

	AppComponent appComponent;
	private DBHelper dbHelper;
	private RxEventBus eventBus;

	@Override
	public void onCreate() {
		super.onCreate();
		Realm.init(this);

		RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
				.name("app.realm")
				.modules(Realm.getDefaultModule(), new FinopassRealmModule())
				.deleteRealmIfMigrationNeeded()
				.build();

		Realm.setDefaultConfiguration(realmConfiguration);
		Realm realm = Realm.getDefaultInstance();
		dbHelper = new DBHelper(realm);
		eventBus = new RxEventBus();

		createDaggerInjections();
	}

	public Context getContext() {
		return getApplicationContext();
	}

	public AppComponent getMyAppComponent() {
		return appComponent;
	}

	private void createDaggerInjections() {
		appComponent = DaggerAppComponent
				.builder()
				.appModule(new AppModule(this, eventBus, dbHelper))
				.build();

		appComponent.inject(this);
	}


}
