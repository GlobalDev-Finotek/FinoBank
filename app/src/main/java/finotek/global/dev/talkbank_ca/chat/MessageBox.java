package finotek.global.dev.talkbank_ca.chat;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.WaitDone;
import finotek.global.dev.talkbank_ca.chat.messages.WaitResult;
import io.reactivex.Flowable;
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

	public void addAndWait(ReceiveMessage msg) {
		Flowable.range(0, 3)
				.observeOn(AndroidSchedulers.mainThread())
				.concatMap(i -> {
					if (i == 0)
						return Flowable.just(new WaitResult());

					if (i == 1)
						return Flowable.just(new WaitDone()).delay(600, TimeUnit.MILLISECONDS);

					if (i == 2)
						return Flowable.just(msg).delay(600, TimeUnit.MILLISECONDS);

					return null;
				})
				.subscribe(observable::onNext);
	}

	public void addAndWait(Object... msg) {
		Flowable.range(0, msg.length + 2)
				.observeOn(AndroidSchedulers.mainThread())
				.concatMap(i -> {
					if (i == 0)
						return Flowable.just(new WaitResult());
					if (i == 1)
						return Flowable.just(new WaitDone()).delay(600, TimeUnit.MILLISECONDS);

					return Flowable.just(msg[i - 2]).delay(600, TimeUnit.MILLISECONDS);
				})
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

	public void removeAt(int index) {
		messages.remove(index);
	}

	public int size() {
		return messages.size();
	}
}
