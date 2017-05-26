package globaldev.finotek.com.logcollector.api.exception;

/**
 * Created by magyeong-ug on 27/04/2017.
 */

public class NoContentException extends Throwable {

	public NoContentException() {
		super("No Contents from response");
	}
}
