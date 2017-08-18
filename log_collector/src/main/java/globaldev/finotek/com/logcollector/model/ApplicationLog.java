package globaldev.finotek.com.logcollector.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.HashMap;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by JungwonSeo on 2017-04-26.
 */
@RealmClass
public class ApplicationLog extends RealmObject implements Parcelable, ValueQueryGenerator {
	public static final Creator<ApplicationLog> CREATOR = new Creator<ApplicationLog>() {
		@Override
		public ApplicationLog createFromParcel(Parcel in) {
			return new ApplicationLog(in);
		}

		@Override
		public ApplicationLog[] newArray(int size) {
			return new ApplicationLog[size];
		}
	};
	public String appName;
	public String startTime;
	public String lastTimeUsed;
	public String packageName;
	public double duration;
	int type = ActionType.GATHER_APP_USAGE_LOG;
	@PrimaryKey
	private String logTime = String.valueOf(System.currentTimeMillis());

	public ApplicationLog() {

	}

	public ApplicationLog(String appName, String startTime, double duration) {
		this.appName = appName;
		this.startTime = startTime;
		this.duration = duration;
	}

	protected ApplicationLog(Parcel in) {
		packageName = in.readString();
		lastTimeUsed = in.readString();
		appName = in.readString();
		startTime = in.readString();
		duration = in.readDouble();
		type = in.readInt();
		logTime = in.readString();
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public String getLogTime() {
		return logTime;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

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
		dest.writeString(packageName);
		dest.writeString(lastTimeUsed);
		dest.writeString(appName);
		dest.writeString(startTime);
		dest.writeDouble(duration);
		dest.writeInt(type);
		dest.writeString(logTime);
	}


	@Override
	public HashMap<String, String> generate() {
		HashMap<String, String> queryMap = new HashMap<>();
		queryMap.put("appName", this.appName);
		queryMap.put("lastTimeUsed", this.lastTimeUsed);
		queryMap.put("duration", String.valueOf(this.duration));
		queryMap.put("logTime", this.logTime);
		queryMap.put("startTime", this.startTime);

		return queryMap;
	}

	@Override
	public int getLogType() {
		return type;
	}
}
