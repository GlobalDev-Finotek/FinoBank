package globaldev.finotek.com.finosign.inject;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import globaldev.finotek.com.finosign.BuildConfig;
import globaldev.finotek.com.finosign.R;
import globaldev.finotek.com.finosign.inject.exception.AlreadyRegisteredException;
import globaldev.finotek.com.finosign.inject.exception.ExceptionCode;
import globaldev.finotek.com.finosign.inject.exception.InvalidSignatureException;
import globaldev.finotek.com.finosign.inject.exception.NotFoundRegisteredSignException;
import globaldev.finotek.com.finosign.inject.response.BaseResponse;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by magyeong-ug on 26/04/2017.
 * Dagger module for organizing API service
 */
@Module
class ApiModule {
	private final Context context;

	public ApiModule(Context context) {
		this.context = context;
	}

	@Provides
	Context context() {
		return context;
	}

	@Provides
	@Singleton
	OkHttpClient provideOkHttpClient(final Context context) {
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

		return new OkHttpClient.Builder()
				.addInterceptor(loggingInterceptor)
				.addInterceptor(new Interceptor() {
					@Override
					public Response intercept(Chain chain) throws IOException {
						Request request = chain.request().newBuilder()
								.addHeader("Authorization", "Bearer" + " " + getToken(context)).build();

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

	@Provides
	@Named("userKey")
	String getUserKey(Context context) {
		return context.getSharedPreferences(context.getString(R.string.finosign_prefs), Context.MODE_PRIVATE)
				.getString(context.getString(R.string.finosign_userkey), "");
	}

	@Provides
	@Named("token")
	String getToken(Context context) {
		return context.getSharedPreferences(context.getString(R.string.finosign_prefs), Context.MODE_PRIVATE)
				.getString(context.getString(R.string.finosign_token), "");
	}

}
