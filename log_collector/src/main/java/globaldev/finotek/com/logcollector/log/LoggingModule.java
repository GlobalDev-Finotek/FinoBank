package globaldev.finotek.com.logcollector.log;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import globaldev.finotek.com.logcollector.app.MyApplication;

/**
 * Created by magyeong-ug on 26/04/2017.
 */
@Module
public class LoggingModule {

	@Inject
	MyApplication application;

	@Inject
	public LoggingModule(MyApplication application) {
		this.application = application;
	}

	@Provides
	@Singleton
	LoggingHelper provideLoggingHelper() {
		return new LoggingHelperImpl(application);
	}
}
