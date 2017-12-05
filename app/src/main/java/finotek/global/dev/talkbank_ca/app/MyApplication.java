package finotek.global.dev.talkbank_ca.app;

import android.app.Application;
import android.content.Context;

import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.inject.component.AppComponent;
import finotek.global.dev.talkbank_ca.inject.component.DaggerAppComponent;
import finotek.global.dev.talkbank_ca.inject.module.AppModule;
import finotek.global.dev.talkbank_ca.model.DBHelper;
import globaldev.finotek.com.finosign.inject.FinoSign;
import io.realm.Realm;

public class MyApplication extends Application {

	AppComponent appComponent;
	private DBHelper dbHelper;
	private RxEventBus eventBus;

	@Override
	public void onCreate() {
		super.onCreate();
		Realm.init(this);
		Realm realm = Realm.getDefaultInstance();
		dbHelper = new DBHelper(realm);
		eventBus = new RxEventBus();

		createDaggerInjections();

		FinoSign.init(this);
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
