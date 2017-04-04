package finotek.global.dev.talkbank_ca.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

// Singleton Instance
public enum MessageBox {
    INSTANCE;

    private final List<Object> messages;
    public final PublishSubject<Object> observable;

    MessageBox(){
        messages = new ArrayList<>();
        observable = PublishSubject.create();
    }

    public void add(Object msg) {
        messages.add(msg);
        observable.onNext(msg);
    }

    public void removeAt(int index){
        messages.remove(index);
    }

    public int size() {
        return messages.size();
    }
}
