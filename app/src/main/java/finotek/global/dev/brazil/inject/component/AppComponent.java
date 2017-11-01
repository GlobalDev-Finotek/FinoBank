package finotek.global.dev.brazil.inject.component;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import finotek.global.dev.brazil.app.MyApplication;
import finotek.global.dev.brazil.base.mvp.event.RxEventBus;
import finotek.global.dev.brazil.inject.module.AppModule;
import finotek.global.dev.brazil.model.DBHelper;
import finotek.global.dev.brazil.util.SharedPrefsHelper;

/**
 * Created by kwm on 2017. 3. 6..
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
	Context context();

	MyApplication myApplication();

	SharedPrefsHelper getPreferenceHelper();

	DBHelper getDBHelper();

	RxEventBus getEventBus();

	void inject(MyApplication app);
}
