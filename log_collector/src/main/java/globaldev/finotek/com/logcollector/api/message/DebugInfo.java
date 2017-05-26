package globaldev.finotek.com.logcollector.api.message;

/**
 * Created by magyeong-ug on 27/04/2017.
 */

public class DebugInfo {
	String error;
	String link;

	public DebugInfo() {
	}

	public DebugInfo(String error, String link) {
		this.error = error;
		this.link = link;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
