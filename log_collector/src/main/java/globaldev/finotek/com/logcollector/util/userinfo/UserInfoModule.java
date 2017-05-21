package globaldev.finotek.com.logcollector.util.userinfo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import globaldev.finotek.com.logcollector.app.MyApplication;

/**
 * Created by magyeong-ug on 02/05/2017.
 */
@Module
public class UserInfoModule {

	MyApplication application;

	public UserInfoModule(MyApplication application) {
		this.application = application;
	}

	@Provides
	@Singleton
	public UserInfoService provideUserInfoGetter(MyApplication application) {
		return new UserInfoGetter(application);
	}

}
