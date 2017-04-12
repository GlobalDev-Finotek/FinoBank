package finotek.global.dev.talkbank_ca.base.mvp.event;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

@Singleton
public class RxEventBus {
	private BehaviorSubject<IEvent> mSubject;

	public RxEventBus() {
		mSubject = BehaviorSubject.create();
	}

	public void sendEvent(IEvent event) {
		mSubject.onNext(event);
	}

	public Observable<IEvent> getObservable() {
		return mSubject;
	}
}