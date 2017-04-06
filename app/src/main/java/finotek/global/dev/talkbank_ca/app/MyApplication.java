package finotek.global.dev.talkbank_ca.app;

import android.app.Application;
import android.content.Context;

import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.inject.component.AppComponent;
import finotek.global.dev.talkbank_ca.inject.component.DaggerAppComponent;
import finotek.global.dev.talkbank_ca.inject.module.AppModule;
import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by kwm on 2017. 3. 6..
 */

public class MyApplication extends Application {

  AppComponent appComponent;
	private RxEventBus eventBus;
	private Realm realm;

	@Override
	public void onCreate() {
		super.onCreate();

		Realm.init(this);

		RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
				.name(Realm.DEFAULT_REALM_NAME)
				.deleteRealmIfMigrationNeeded()
				.build();

		Realm.setDefaultConfiguration(realmConfiguration);
		realm = Realm.getDefaultInstance();
		eventBus = new RxEventBus();


		createDaggerInjections();
	}

	public Context getContext() {
		return getApplicationContext();
	}

  public AppComponent getAppComponent() {
    return appComponent;
  }

  private void createDaggerInjections() {
    appComponent = DaggerAppComponent
        .builder()
		    .appModule(new AppModule(this, eventBus, realm))
		    .build();

    appComponent.inject(this);
  }


}
