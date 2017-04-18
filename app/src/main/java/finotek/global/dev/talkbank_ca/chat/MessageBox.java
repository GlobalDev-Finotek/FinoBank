package finotek.global.dev.talkbank_ca.chat;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.chat.messages.WaitForMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.EnableToEditMoney;
import finotek.global.dev.talkbank_ca.chat.messages.contact.SelectedContact;
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

    public void add(Object msg) {
        messages.add(msg);

	    Log.d("FINO-TB", "Message Received: " + msg.getClass().getName());

        Flowable.interval(200, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .first((long) 1)
            .subscribe(value -> {
                if (!(msg instanceof EnableToEditMoney) && !(msg instanceof SelectedContact)) {
                    observable.onNext(new WaitForMessage());
                }

                observable.onNext(msg);
            });
    }

    public void delay(Object msg, int delay){
        messages.add(msg);

        Flowable.interval(delay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .first((long) 1)
                .subscribe(value -> {
                    if (!(msg instanceof EnableToEditMoney) && !(msg instanceof SelectedContact)) {
                        observable.onNext(new WaitForMessage());
                    }

                    observable.onNext(msg);
                });
    }

    public void removeAt(int index) {
        messages.remove(index);
    }

    public int size() {
        return messages.size();
    }
}
