package globaldev.finotek.com.logcollector.log;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import globaldev.finotek.com.logcollector.R;
import globaldev.finotek.com.logcollector.api.log.ApiServiceImpl;
import globaldev.finotek.com.logcollector.api.log.LogApi;
import globaldev.finotek.com.logcollector.app.AppModule;
import globaldev.finotek.com.logcollector.util.LogUtil;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by magyeong-ug on 26/04/2017.
 * Base form of logging service.
 */
public abstract class BaseLoggingService<T extends RealmObject> extends JobService {

	public int JOB_ID;
	protected Realm realm;
	protected List<T> logData = new ArrayList<>();
	protected ApiServiceImpl logService;


	BaseLoggingService() {


	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.realm = Realm.getDefaultInstance();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://dvikqteix2.execute-api.ap-northeast-2.amazonaws.com/prod/finopass/")
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
				.client(new OkHttpClient.Builder()
						.connectTimeout(1, TimeUnit.MINUTES)
						.cache(new Cache(getApplication().getCacheDir(), 10 * 10 * 1024))
						.build())
				.build();
		logService = new ApiServiceImpl(retrofit.create(LogApi.class));
	}

	protected void parse(boolean isGetAllData) {
		getData(isGetAllData);
	}

	protected abstract Class getDBClass();

	public abstract void getData(boolean isGetAllData);

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
	@Override
	public boolean onStartJob(JobParameters params) {
		boolean isGetallData = params.getExtras().getBoolean("isGetAllData");

		LogUtil.write(getClass().getCanonicalName() + " parse started");
		parse(isGetallData);
		LogUtil.write(getClass().getCanonicalName() + " parse ended");

		RealmResults rl = realm.where(getDBClass()).findAll();
		List<T> copiedFromDB = realm.copyFromRealm(rl);

		Set<T> set = new HashSet<>();
		set.addAll(copiedFromDB);
		set.addAll(logData);

		uploadLogs(params, new ArrayList<>(set), isGetallData);
		return true;
	}

	@Override
	public boolean onStopJob(JobParameters params) {
		saveLogs();
		return true;
	}

	protected void uploadLogs(final JobParameters params, final List<T> logs, boolean isGetAllData) {

		if (logs.size() == 0) return;

		String userKey = getSharedPreferences(AppModule.SHARED_PREFS, MODE_PRIVATE).getString(
				getBaseContext().getString(R.string.user_key), "");

		logService.upload(userKey, JOB_ID, logs, isGetAllData)
				.retry(3)
				.subscribe(new Consumer() {
					@Override
					public void accept(Object o) throws Exception {
						System.out.println(o);
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						saveLogs();
						jobFinished(params, true);
					}
				}, new Action() {
					@Override
					public void run() throws Exception {
						jobFinished(params, false);
						clearDB(logs.get(0).getClass());
					}
				});


	}

	protected void saveLogs() {
		realm.beginTransaction();
		realm.insertOrUpdate(logData);
		realm.commitTransaction();
		realm.close();
	}

	protected void clearDB(Class clazz) {
		realm.beginTransaction();
		realm.delete(clazz);
		realm.commitTransaction();
		realm.close();
	}

	private void writeLogString(T logData) {
		LogUtil.write(logData.toString() + "\n");
	}

}