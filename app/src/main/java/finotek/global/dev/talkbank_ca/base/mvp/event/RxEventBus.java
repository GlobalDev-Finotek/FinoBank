package finotek.global.dev.talkbank_ca.base.mvp.event;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

@Singleton
public class RxEventBus {
	private List<IEvent> events;

	public RxEventBus() {
		events = new ArrayList<>();
	}

	public void sendEvent(IEvent event) {
		events.add(event);
	}

	public Observable<IEvent> getObservable() {
		return Observable.create(subscriber -> {
			for(IEvent event : events) {
				subscriber.onNext(event);
			}
		});
	}

	public void clear(){

	}
}