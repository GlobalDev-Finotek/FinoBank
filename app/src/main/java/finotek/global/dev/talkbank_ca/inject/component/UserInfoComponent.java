package finotek.global.dev.talkbank_ca.inject.component;

import dagger.Component;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.inject.scope.PerActivity;
import finotek.global.dev.talkbank_ca.user.UserInfoFragment;
import finotek.global.dev.talkbank_ca.user.UserRegistrationActivity;

/**
 * Created by magyeong-ug on 2017. 4. 6..
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface UserInfoComponent {
	void inject(UserRegistrationActivity activity);

	void inject(UserInfoFragment fragment);
}
