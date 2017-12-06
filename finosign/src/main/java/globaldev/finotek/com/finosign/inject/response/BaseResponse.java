package globaldev.finotek.com.finosign.inject.response;

/**
 * Created by magyeong-ug on 2017. 12. 6..
 */

public class BaseResponse<T> {
	public int statusCode;
	public T data;
	public String message;
}
