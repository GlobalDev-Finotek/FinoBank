package globaldev.finotek.com.logcollector.app;

import android.app.Application;

import globaldev.finotek.com.logcollector.api.ApiModule;
import globaldev.finotek.com.logcollector.log.LoggingModule;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoModule;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by magyeong-ug on 26/04/2017.
 */

public class MyApplication extends Application {

	AppComponent appComponent;

	public AppComponent getAppComponent() {
		return appComponent;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Realm.init(this);

		RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
				.name(Realm.DEFAULT_REALM_NAME)
				.deleteRealmIfMigrationNeeded()
				.build();

		Realm.setDefaultConfiguration(realmConfiguration);


		AppModule appModule = new AppModule(this);

		appComponent = DaggerAppComponent.builder()
				.appModule(appModule)
				.userInfoModule(new UserInfoModule(this))
				.loggingModule(new LoggingModule(this))
				.apiModule(new ApiModule())
				.build();


	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Realm.getDefaultInstance().close();
	}
}
