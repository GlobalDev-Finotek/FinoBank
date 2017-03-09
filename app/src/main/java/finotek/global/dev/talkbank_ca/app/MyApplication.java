package finotek.global.dev.talkbank_ca.app;

import android.app.Application;
import android.content.Context;

import finotek.global.dev.talkbank_ca.inject.component.AppComponent;
import finotek.global.dev.talkbank_ca.inject.component.DaggerAppComponent;
import finotek.global.dev.talkbank_ca.inject.module.AppModule;


/**
 * Created by kwm on 2017. 3. 6..
 */

public class MyApplication extends Application {

  AppComponent appComponent;

  public static MyApplication getAppInstance(Context context) {
    return (MyApplication) context.getApplicationContext();
  }

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

  private void createDaggerInjections() {
    appComponent = DaggerAppComponent
        .builder()
        .appModule(new AppModule(this))
        .build();

    appComponent.inject(this);
  }


}
