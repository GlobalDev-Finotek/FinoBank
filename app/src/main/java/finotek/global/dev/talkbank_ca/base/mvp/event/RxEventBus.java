package finotek.global.dev.talkbank_ca.base.mvp.event;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

@Singleton
public class RxEventBus {
	private BehaviorSubject<IEvent> subject;

	public RxEventBus() {
		subject = BehaviorSubject.create();
	}

	public void sendEvent(IEvent event) {
		subject.onNext(event);
	}

	public Observable<IEvent> getObservable() {
		return subject;
	}

	public void clear(){
		subject.onComplete();
		subject = BehaviorSubject.create();
	}
}