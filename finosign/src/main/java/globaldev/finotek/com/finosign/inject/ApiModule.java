package globaldev.finotek.com.finosign.inject;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import globaldev.finotek.com.finosign.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by magyeong-ug on 26/04/2017.
 * Dagger module for organizing API service
 */
@Module
public class ApiModule {


	@Provides
	@Singleton
	OkHttpClient provideOkHttpClient() {
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

		return new OkHttpClient.Builder()
				.addInterceptor(loggingInterceptor)
				.addInterceptor(new Interceptor() {
					@Override
					public Response intercept(Chain chain) throws IOException {
						Request request = chain.request().newBuilder()
								.addHeader("Authorization", "Bearer" + " " + BuildConfig.FINOSIGN_API_KEY).build();
						return chain.proceed(request);
					}
				})
				.connectTimeout(1, TimeUnit.MINUTES)
				.build();
	}

	@Provides
	@Singleton
	Gson provideGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		return gsonBuilder.create();
	}

	@Provides
	@Singleton
	Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
		return new Retrofit.Builder()
				.baseUrl(BuildConfig.FINOSIGN_BASE_URL)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(MyGsonConverterFactory.create(gson))
				.client(okHttpClient)
				.build();
	}

	@Provides
	@Singleton
	FinoSignApi provideUserApiService(Retrofit retrofit) {
		return retrofit.create(FinoSignApi.class);
	}



}
