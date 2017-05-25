package globaldev.finotek.com.logcollector.api.message;

import com.google.gson.annotations.SerializedName;

import globaldev.finotek.com.logcollector.api.exception.NoContentException;
import io.reactivex.Flowable;

/**
 * Created by magyeong-ug on 26/04/2017.
 * Base response received from server. It must contain header, body and message type code.
 */

public class BaseResponse<T> {
	@SerializedName("header")
	private Header header;
	@SerializedName("body")
	private T body;
	@SerializedName("debug")
	private DebugInfo debug;


	public BaseResponse() {
	}

	public static <T> Flowable<T> body(BaseResponse<T> response) {
		int stateCode = response.getHeader().getErrorCode();
		if (stateCode != 0) {
			return Flowable.error(new NoContentException());
		}

		return Flowable.just(response.getBody());
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}

	public DebugInfo getDebug() {
		return debug;
	}

	public void setDebug(DebugInfo debug) {
		this.debug = debug;
	}
}
