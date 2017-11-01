package finotek.global.dev.brazil.inject.component;

import dagger.Component;
import finotek.global.dev.brazil.chat.ChatActivity;
import finotek.global.dev.brazil.inject.module.ActivityModule;
import finotek.global.dev.brazil.inject.scope.PerActivity;

/**
 * Created by magyeong-ug on 2017. 4. 6..
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = {ActivityModule.class})
public interface ChatComponent {
	void inject(ChatActivity activity);
}
