package finotek.global.dev.talkbank_ca.inject.component;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.inject.module.AppModule;
import finotek.global.dev.talkbank_ca.util.SharedPrefsHelper;
import io.realm.Realm;

/**
 * Created by kwm on 2017. 3. 6..
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
  Context context();
  MyApplication myApplication();
  SharedPrefsHelper getPreferenceHelper();

	Realm getRealm();

	RxEventBus getEventBus();

	void inject(MyApplication app);
}
