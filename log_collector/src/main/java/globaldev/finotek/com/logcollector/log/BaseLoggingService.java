package globaldev.finotek.com.logcollector.log;

import android.app.job.JobParameters;
import android.app.job.JobService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import globaldev.finotek.com.logcollector.util.LogUtil;
import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by magyeong-ug on 26/04/2017.
 * Base form of logging service.
 */
public abstract class BaseLoggingService<T extends RealmObject> extends JobService {

	protected Realm realm;
	protected List<T> logData = new ArrayList<>();

	BaseLoggingService() {
		this.realm = Realm.getDefaultInstance();
	}


	/**
	 * Parse context log. it is different from each logging services.
	 */
	protected abstract void parse();

	protected abstract void notifyJobDone(List<T> logData);


	@Override
	public boolean onStartJob(JobParameters params) {
		LogUtil.write(getClass().getCanonicalName() + " parse started");
		parse();
		LogUtil.write(getClass().getCanonicalName() + " parse ended");
		realm.beginTransaction();
		ArrayList<T> dataFromDB = new ArrayList<>();
		realm.copyFromRealm(dataFromDB);
		realm.commitTransaction();

		Set<T> set = new HashSet<>();
		set.addAll(dataFromDB);
		set.addAll(logData);

		writeLogString(logData);

		notifyJobDone(new ArrayList<T>(set));

		return true;
	}

	private void writeLogString(List<T> logData) {

		for(T t : logData) {
			LogUtil.write(t.toString() + "\n");
		}
	}


	@Override
	public boolean onStopJob(JobParameters params) {
		realm.beginTransaction();
		realm.insertOrUpdate(logData);
		realm.commitTransaction();
		realm.close();
		return true;
	}


}