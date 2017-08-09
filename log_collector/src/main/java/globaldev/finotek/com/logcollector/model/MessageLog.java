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
public class MessageLog extends RealmObject implements Parcelable {

	public static final Creator<MessageLog> CREATOR = new Creator<MessageLog>() {
		@Override
		public MessageLog createFromParcel(Parcel in) {
			return new MessageLog(in);
		}

		@Override
		public MessageLog[] newArray(int size) {
			return new MessageLog[size];
		}
	};
	public boolean isSent;
	public String targetNumber;
	public String targetName;
	public long timestamp;
	public int length;
	public String text;

	int type = ActionType.GATHER_MESSAGE_LOG;
	@PrimaryKey
	private String logTime = String.valueOf(System.currentTimeMillis());

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public MessageLog() {

	}

	protected MessageLog(Parcel in) {
		isSent = in.readByte() != 0;
		targetNumber = in.readString();
		targetName = in.readString();
		length = in.readInt();
		text = in.readString();
		logTime = in.readString();
		type = in.readInt();
	}

	public String getLogTime() {
		return logTime;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setSent(boolean sent) {
		isSent = sent;
	}

	public String getTargetNumber() {
		return targetNumber;
	}

	public void setTargetNumber(String targetNumber) {
		this.targetNumber = targetNumber;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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
		dest.writeByte((byte) (isSent ? 1 : 0));
		dest.writeString(targetNumber);
		dest.writeString(targetName);
		dest.writeInt(length);
		dest.writeString(text);
		dest.writeString(logTime);
		dest.writeInt(type);
	}
}
