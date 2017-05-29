package globaldev.finotek.com.logcollector.db;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by magyeong-ug on 26/04/2017.
 */

public interface DBHelper<T> {
	Observable<T> get(Class clazz);

	void insert(List<T> item);
}
