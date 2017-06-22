package globaldev.finotek.com.logcollector.model;

import com.google.gson.Gson;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
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
	int type = ActionType.GATHER_CALL_LOG;
	@PrimaryKey
	private String logTime = String.valueOf(System.currentTimeMillis());
	private String timestamp;

	public CallHistoryLog() {

	}

	public CallHistoryLog(String timestamp, boolean isSent, String duration, String targetNumber, String targetName) {
		this.timestamp = timestamp;
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


