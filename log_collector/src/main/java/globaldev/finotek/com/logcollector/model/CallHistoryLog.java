package globaldev.finotek.com.logcollector.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by JungwonSeo on 2017-04-26.
 */
@RealmClass
public class CallHistoryLog extends RealmObject implements Parcelable {
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


	protected CallHistoryLog(Parcel in) {
		isSent = in.readByte() != 0;
		duration = in.readString();
		targetNumber = in.readString();
		targetName = in.readString();
		logTime = in.readString();
		type = in.readInt();
	}

	public static final Creator<CallHistoryLog> CREATOR = new Creator<CallHistoryLog>() {
		@Override
		public CallHistoryLog createFromParcel(Parcel in) {
			return new CallHistoryLog(in);
		}

		@Override
		public CallHistoryLog[] newArray(int size) {
			return new CallHistoryLog[size];
		}
	};

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (isSent ? 1 : 0));
		dest.writeString(duration);
		dest.writeString(targetNumber);
		dest.writeString(targetName);
		dest.writeString(logTime);
		dest.writeInt(type);
	}
}


