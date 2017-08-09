package globaldev.finotek.com.logcollector.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import globaldev.finotek.com.logcollector.api.file.FileApi;
import globaldev.finotek.com.logcollector.api.file.FileServiceImpl;
import globaldev.finotek.com.logcollector.api.log.ApiServiceImpl;
import globaldev.finotek.com.logcollector.api.log.LogApi;
import globaldev.finotek.com.logcollector.api.user.UserService;
import globaldev.finotek.com.logcollector.api.user.UserServiceImpl;
import globaldev.finotek.com.logcollector.app.FinopassApp;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by magyeong-ug on 26/04/2017.
 * Dagger module for organizing API service
 */
@Module
public class ApiModule {

	private static final String BASE_URL = "https://dvikqteix2.execute-api.ap-northeast-2.amazonaws.com/prod/finopass/";

	/**
	 * Provide ok http cache cache.
	 * Default cache size is 10 MB
	 *
	 * @param application application instance of app
	 * @return the cache
	 */
	@Provides
	@Singleton
	Cache provideOkHttpCache(FinopassApp application) {
		int CACHE_SIZE = 10 * 1024 * 1024;
		return new Cache(application.getCacheDir(), CACHE_SIZE);
	}

	/**
	 * Provide gson factory with all fields are converted to lowercase with underscore.
	 * ex) someFieldName ---> some_field_name
	 *
	 * @return the gson
	 */
	@Provides
	@Singleton
	Gson provideGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		return gsonBuilder.create();
	}

	/**
	 * Provide ok http client.
	 *
	 * @param cache the cache
	 * @return the ok http client
	 */
	@Provides
	@Singleton
	OkHttpClient provideOkHttpClient(Cache cache) {
		return new OkHttpClient.Builder()
				.connectTimeout(1, TimeUnit.MINUTES)
				.cache(cache)
				.build();
	}

	/**
	 * Provide retrofit retrofit.
	 *
	 * @param gson         the gson
	 * @param okHttpClient the ok http client
	 * @return the retrofit
	 */
	@Provides
	@Singleton
	Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
		return new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create(gson))
				.client(okHttpClient)
				.build();
	}

	/**
	 * Provide user api service.
	 *
	 * @param retrofit the retrofit
	 * @return the user service
	 */
	@Provides
	@Singleton
	UserService provideUserApiService(Retrofit retrofit) {
		return retrofit.create(UserService.class);
	}

	/**
	 * Provide user api service.
	 *
	 * @param userService injected by in this module
	 * @return the user service
	 */
	@Provides
	@Singleton
	UserServiceImpl provideUserServiceImpl(UserService userService) {
		return new UserServiceImpl(userService);
	}


	/**
	 * Provide push api service.
	 *
	 * @param retrofit the retrofit
	 * @return the push service
	 */
	@Provides
	@Singleton
	LogApi provideLogApiService(Retrofit retrofit) {
		return retrofit.create(LogApi.class);
	}

	/**
	 * Provide push api service.
	 *
	 * @param logService injected by in this module
	 * @return the push service
	 */
	@Provides
	@Singleton
	ApiServiceImpl provideLogServiceImpl(LogApi logService) {
		return new ApiServiceImpl(logService);
	}

	/**
	 * Provide file api service.
	 *
	 * @param retrofit the retrofit
	 * @return file service
	 */
	@Provides
	@Singleton
	FileApi provideFileApiService(Retrofit retrofit) {
		return retrofit.create(FileApi.class);
	}

	/**
	 * Provide push api service.
	 *
	 * @param fileApi injected by in this module
	 * @return the push service
	 */
	@Provides
	@Singleton
	FileServiceImpl provideFileServiceImpl(FileApi fileApi) {
		return new FileServiceImpl(fileApi);
	}
}
