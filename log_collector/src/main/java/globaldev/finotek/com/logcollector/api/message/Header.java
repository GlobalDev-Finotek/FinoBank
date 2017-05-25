package globaldev.finotek.com.logcollector.api.message;

/**
 * Created by magyeong-ug on 27/04/2017.
 */

public class Header {

	private int errorCode;
	private String message;


	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


}
