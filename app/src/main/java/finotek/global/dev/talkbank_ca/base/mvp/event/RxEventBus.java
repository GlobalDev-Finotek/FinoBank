package finotek.global.dev.talkbank_ca.base.mvp.event;

import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by magyeong-ug on 2017. 3. 31..
 */
@Singleton
public class RxEventBus {

	private PublishSubject<IEvent> mSubject;

	public RxEventBus() {
		mSubject = PublishSubject.create();
	}

	public void sendEvent(IEvent event) {
		mSubject.onNext(event);
	}

	public Observable<IEvent> getObservable() {
		return Observable.defer(() -> mSubject);
	}
}

