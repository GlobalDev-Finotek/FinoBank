package globaldev.finotek.com.logcollector.model;

import com.google.gson.Gson;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by JungwonSeo on 2017-04-26.
 */
@RealmClass
public class CallHistoryLog extends RealmObject {
	public boolean isSent;
	public String duration;
	public String targetNumber;
	public String targetName;
	protected String logTime;
	int type = ActionType.GATHER_CALL_LOG;

	public CallHistoryLog() {

	}

	public CallHistoryLog(String logTime, boolean isSent, String duration, String targetNumber, String targetName) {
		this.logTime = logTime;
		this.isSent = isSent;
		this.duration = duration;
		this.targetNumber = targetNumber;
		this.targetName = targetName;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}


