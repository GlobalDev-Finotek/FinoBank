package finotek.global.dev.talkbank_ca.chat;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.chat.messages.action.EnableToEditMoney;
import finotek.global.dev.talkbank_ca.chat.messages.contact.SelectedContact;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.BehaviorSubject;

// Singleton Instance
public enum MessageBox {
	INSTANCE;

	public final BehaviorSubject<Object> observable;
	private final List<Object> messages;

	MessageBox() {
		messages = new ArrayList<>();
		observable = BehaviorSubject.create();
	}

	public void add(Object... msg) {
		Flowable.range(0, msg.length)
				.observeOn(AndroidSchedulers.mainThread())
				.concatMap(i -> Flowable.just(msg[i]).delay(500, TimeUnit.MILLISECONDS))
				.subscribe(observable::onNext);
	}

	public void add(Object msg, int delay) {
		messages.add(msg);

		Log.d("FINO-TB", "Message Received: " + msg.getClass().getName());

		Flowable.interval(delay, TimeUnit.MILLISECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.first((long) 1)
				.subscribe(value -> {
					observable.onNext(msg);
				});
	}

	public void add(Object msg) {
		add(msg, 200);
	}

	public void delay(Object msg, int delay) {
		add(msg, delay);
	}

	public void removeAt(int index) {
		messages.remove(index);
	}

	public int size() {
		return messages.size();
	}
}
