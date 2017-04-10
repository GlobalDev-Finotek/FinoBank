package finotek.global.dev.talkbank_ca.model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;

/**
 * Created by magyeong-ug on 2017. 4. 10..
 */

public class DBHelper {

	private Realm realm;

	public DBHelper(Realm realm) {
		this.realm = realm;
	}

	public <T extends RealmObject> Observable<RealmResults<T>> get(Class<T> clazz) {
		try {
			return realm.where(clazz).findAll().asObservable();
		} catch (IndexOutOfBoundsException e) {
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
