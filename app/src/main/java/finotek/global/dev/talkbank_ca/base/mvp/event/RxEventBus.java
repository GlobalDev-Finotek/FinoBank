package finotek.global.dev.talkbank_ca.base.mvp.event;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by magyeong-ug on 2017. 3. 31..
 */

public class RxEventBus {

	private static RxEventBus mInstance;
	private BehaviorSubject<IEvent> mSubject;

	private RxEventBus() {
		mSubject = BehaviorSubject.create();
	}

	public static RxEventBus getInstance() {
		if (mInstance == null) {
			mInstance = new RxEventBus();
		}
		return mInstance;
	}

	public void sendEvent(IEvent event) {
		mSubject.onNext(event);
	}

	public Observable<IEvent> getObservable() {
		return Observable.defer(() -> mSubject);
	}
}

