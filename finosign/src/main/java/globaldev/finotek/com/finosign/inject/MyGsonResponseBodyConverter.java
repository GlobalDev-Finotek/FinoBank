package globaldev.finotek.com.finosign.inject;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by magyeong-ug on 2017. 10. 24..
 */

class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
	private final Gson gson;
	private final TypeAdapter<T> adapter;

	MyGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
		this.gson = gson;
		this.adapter = adapter;
	}

	@Override
	public T convert(ResponseBody value) throws IOException {
		JsonReader jsonReader = new JsonReader(value.charStream());
		try {
			return adapter.read(jsonReader);
		} finally {
			value.close();
		}
	}
}
