package globaldev.finotek.com.logcollector.api.message;


import com.google.gson.annotations.SerializedName;

/**
 * Created by kwm on 2016. 6. 21..
 */
public class BaseRequest<T> {

	@SerializedName("header")
	private Header header;

	@SerializedName("param")
	private T param;

	public BaseRequest(Builder<T> builder) {
		this.header = builder.header;
		this.param = builder.param;
	}

	public Header getHeader() {
		return header;
	}

	public T getParam() {
		return param;
	}

	public static class Builder<T> {
		private T param;
		private Header header;

		public Builder() {
		}

		public Builder<T> setHeader(Header header) {
			this.header = header;
			return this;
		}

		public Builder<T> setParam(T param) {
			this.param = param;
			return this;
		}

		public BaseRequest<T> build() {
			return new BaseRequest<>(this);
		}
	}
}
