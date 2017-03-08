package finotek.global.dev.talkbank_ca.inject.module;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import finotek.global.dev.talkbank_ca.app.MyApplication;

/**
 * Created by kwm on 2017. 3. 6..
 */
@Module
public class AppModule {

  @NonNull
  MyApplication application;

  public AppModule(@NonNull MyApplication application) {
    this.application = application;
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



}
