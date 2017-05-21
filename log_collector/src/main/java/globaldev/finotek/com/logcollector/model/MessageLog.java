package globaldev.finotek.com.logcollector.model;

import com.google.gson.Gson;

import io.realm.RealmObject;

/**
 * Created by JungwonSeo on 2017-04-26.
 */

public class MessageLog extends RealmObject {

	public boolean isSent;
	public String targetNumber;
	public String targetName;
	public int length;
	public String text;
	protected String logTime;
	int type = ActionType.GATHER_MESSAGE_LOG;

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
}
