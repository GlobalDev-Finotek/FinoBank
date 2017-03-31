package finotek.global.dev.talkbank_ca.chat;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

public class MessageBox {
    private final List<Object> messages;
    private final PublishSubject<Object> ps;

    public MessageBox(){
        messages = new ArrayList<>();
        ps = PublishSubject.create();
    }

    public void add(Object msg) {
        messages.add(msg);
        ps.onNext(msg);
    }

    public int size() {
        return messages.size();
    }

    public Observable<Object> getObservable() {
        return ps;
    }
}
