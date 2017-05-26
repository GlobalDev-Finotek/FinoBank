package globaldev.finotek.com.logcollector.util.eventbus;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import globaldev.finotek.com.logcollector.model.ActionType;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by magyeong-ug on 04/05/2017.
 */

public class RxEventBus {


	public static final int REGISTER_FCM = 0;
	public static final int PARSING_APP_USAGE_FINISHED = ActionType.GATHER_APP_USAGE_LOG;
	public static final int PARSING_SMS_FINISHED = ActionType.GATHER_MESSAGE_LOG;
	public static final int PARSING_CALL_FINISHED = ActionType.GATHER_CALL_LOG;
	public static final int PARSING_LOCATION_FINISHED = ActionType.GATHER_LOCATION_LOG;
	public static final int PARSING_SECURITY_FINISHED = ActionType.GATHER_DEVICE_SECURITY_LOG;


	private static SparseArray<BehaviorSubject<Object>> sSubjectMap = new SparseArray<>();
	private static Map<Object, CompositeDisposable> sSubscriptionsMap = new HashMap<>();

	@Inject
	public RxEventBus() {
		// hidden constructor
	}

	/**
	 * Get the subject or create it if it's not already in memory.
	 */
	@NonNull
	private static BehaviorSubject<Object> getSubject(@Subject int subjectCode) {
		BehaviorSubject<Object> subject = sSubjectMap.get(subjectCode);
		if (subject == null) {
			subject = BehaviorSubject.create();
			subject.subscribeOn(AndroidSchedulers.mainThread());
			sSubjectMap.put(subjectCode, subject);
		}

		return subject;
	}

	/**
	 * Get the CompositeSubscription or create it if it's not already in memory.
	 */

	@NonNull
	private static CompositeDisposable getCompositeSubscription(@NonNull Object object) {
		CompositeDisposable compositeSubscription = sSubscriptionsMap.get(object);
		if (compositeSubscription == null) {
			compositeSubscription = new CompositeDisposable();
			sSubscriptionsMap.put(object, compositeSubscription);
		}

		return compositeSubscription;
	}

	/**
	 * Subscribe to the specified subject and listen for updates on that subject. Pass in an object to associate
	 * your registration with, so that you can unsubscribe later.
	 * <br/><br/>
	 * <b>Note:</b> Make sure to call {@link RxEventBus#unregister(Object)} to avoid memory leaks.
	 */
	public void subscribe(@Subject int subject, @NonNull Object lifecycle, @NonNull Consumer<Object> action) {
		Disposable subscription = getSubject(subject).subscribe(action);
		getCompositeSubscription(lifecycle).add(subscription);
	}

	/**
	 * Unregisters this object from the bus, removing all subscriptions.
	 * This should be called when the object is going to go out of memory.
	 */
	public void unregister(@NonNull Object lifecycle) {
		//We have to remove the composition from the map, because once you unsubscribe it can't be used anymore
		CompositeDisposable compositeSubscription = sSubscriptionsMap.remove(lifecycle);
		if (compositeSubscription != null) {
			compositeSubscription.dispose();
		}
	}

	/**
	 * Publish an object to the specified subject for all subscribers of that subject.
	 */
	public void publish(@Subject int subject, @NonNull Object message) {
		getSubject(subject).onNext(message);
	}

	@Retention(SOURCE)
	@IntDef({REGISTER_FCM, PARSING_CALL_FINISHED,
			PARSING_APP_USAGE_FINISHED, PARSING_LOCATION_FINISHED,
			PARSING_SECURITY_FINISHED, PARSING_SMS_FINISHED})
	@interface Subject {
	}

}
