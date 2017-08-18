package globaldev.finotek.com.logcollector.log;

import android.app.Application;


/**
 * Created by magyeong-ug on 26/04/2017.
 */
public class LoggingModule {

	Application application;

	public LoggingModule(Application application) {
		this.application = application;
	}

	LoggingHelper provideLoggingHelper() {
		return new LoggingHelperImpl(application);
	}
}
