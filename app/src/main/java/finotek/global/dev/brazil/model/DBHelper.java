package finotek.global.dev.brazil.model;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by magyeong-ug on 2017. 4. 10..
 */

public class DBHelper {

	private Realm realm;

	@Inject
	public DBHelper(Realm realm) {
		this.realm = realm;
	}

	public <T extends RealmObject> Observable<RealmResults<T>> get(Class<T> clazz) {
		try {
			return Observable.just(realm.where(clazz).findAll());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Observable.empty();
	}

	public <T extends RealmObject> void insert(T t) {
		realm.beginTransaction();
		realm.insertOrUpdate(t);
		realm.commitTransaction();
	}

}
