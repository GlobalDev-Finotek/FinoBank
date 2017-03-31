package finotek.global.dev.talkbank_ca.inject.component;

import dagger.Component;
import finotek.global.dev.talkbank_ca.MainActivity;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.inject.scope.PerActivity;

/**
 * Created by magyeong-ug on 21/03/2017.
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface MainComponent {
	void inject(MainActivity activity);
}
