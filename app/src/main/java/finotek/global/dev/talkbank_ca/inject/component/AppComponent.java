package finotek.global.dev.talkbank_ca.inject.component;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.inject.module.AppModule;
import finotek.global.dev.talkbank_ca.util.SharedPrefsHelper;

/**
 * Created by kwm on 2017. 3. 6..
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
  Context context();
  MyApplication myApplication();
  SharedPrefsHelper getPreferenceHelper();
  void inject(MyApplication app);
}
