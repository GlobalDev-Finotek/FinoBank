package globaldev.finotek.com.logcollector.util.userinfo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import globaldev.finotek.com.logcollector.app.FinopassApp;

/**
 * Created by magyeong-ug on 02/05/2017.
 */
@Module
public class UserInfoModule {

	FinopassApp application;

	public UserInfoModule(FinopassApp application) {
		this.application = application;
	}

	@Provides
	@Singleton
	public UserInfoService provideUserInfoGetter(FinopassApp application) {
		return new UserInfoGetter(application);
	}

}
