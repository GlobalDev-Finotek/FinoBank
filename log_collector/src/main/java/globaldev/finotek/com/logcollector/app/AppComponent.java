package globaldev.finotek.com.logcollector.app;

import javax.inject.Singleton;

import dagger.Component;
import globaldev.finotek.com.logcollector.InitActivity;
import globaldev.finotek.com.logcollector.MyFirebaseInstanceIDService;
import globaldev.finotek.com.logcollector.MyFirebaseMessagingService;
import globaldev.finotek.com.logcollector.api.ApiModule;
import globaldev.finotek.com.logcollector.log.AppUsageLoggingService;
import globaldev.finotek.com.logcollector.log.CallLogHistoryLoggingService;
import globaldev.finotek.com.logcollector.log.LocationLogService;
import globaldev.finotek.com.logcollector.log.LoggingHelperImpl;
import globaldev.finotek.com.logcollector.log.LoggingModule;
import globaldev.finotek.com.logcollector.log.SMSLoggingService;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoModule;

/**
 * Created by magyeong-ug on 26/04/2017.
 */
@Singleton
@Component(modules = {AppModule.class, UserInfoModule.class,
		LoggingModule.class, ApiModule.class})
public interface AppComponent {
	void inject(MyFirebaseInstanceIDService myFirebaseInstanceIDService);

	void inject(MyFirebaseMessagingService myFirebaseMessagingService);

	void inject(LoggingHelperImpl loggingHelper);

	void inject(CallLogHistoryLoggingService callLogHistoryLoggingService);


	void inject(InitActivity initActivity);

	void inject(LocationLogService locationLogService);

	void inject(SMSLoggingService smsLoggingService);

	void inject(AppUsageLoggingService appUsageLoggingService);


}
