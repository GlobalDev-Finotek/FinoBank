package globaldev.finotek.com.logcollector.log;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

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

	public int JOB_ID;
	protected Realm realm;
	protected List<T> logData = new ArrayList<>();

	BaseLoggingService() {
		this.realm = Realm.getDefaultInstance();
	}

	protected void parse(boolean isGetAllData) {
		logData.addAll(getData(isGetAllData));
		notifyJobDone(logData);
	}

	public abstract List<T> getData(boolean isGetAllData);

	protected abstract void notifyJobDone(List<T> logData);

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
	@Override
	public boolean onStartJob(JobParameters params) {
		boolean isGetallData = params.getExtras().getBoolean("isGetAllData");

		LogUtil.write(getClass().getCanonicalName() + " parse started");
		parse(isGetallData);
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