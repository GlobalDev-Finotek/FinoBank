package finotek.global.dev.brazil.inject.component;

import dagger.Component;
import finotek.global.dev.brazil.MainActivity;
import finotek.global.dev.brazil.inject.module.ActivityModule;
import finotek.global.dev.brazil.inject.scope.PerActivity;

/**
 * Created by magyeong-ug on 21/03/2017.
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface MainComponent {
	void inject(MainActivity activity);

}
