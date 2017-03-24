package finotek.global.dev.talkbank_ca.app;

import android.app.Application;
import android.content.Context;

import finotek.global.dev.talkbank_ca.inject.component.AppComponent;
import finotek.global.dev.talkbank_ca.inject.component.DaggerAppComponent;
import finotek.global.dev.talkbank_ca.inject.component.DaggerMainComponent;
import finotek.global.dev.talkbank_ca.inject.component.MainComponent;
import finotek.global.dev.talkbank_ca.inject.module.AppModule;


/**
 * Created by kwm on 2017. 3. 6..
 */

public class MyApplication extends Application {

  AppComponent appComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    createDaggerInjections();
  }

  public Context getContext() {
    return this.getApplicationContext();
  }

  public AppComponent getAppComponent() {
    return appComponent;
  }

	public MainComponent getMainComponent() {
		return DaggerMainComponent.builder()
				.appComponent(appComponent).build();
	}

  private void createDaggerInjections() {
    appComponent = DaggerAppComponent
        .builder()
        .appModule(new AppModule(this))
        .build();

    appComponent.inject(this);
  }


}
