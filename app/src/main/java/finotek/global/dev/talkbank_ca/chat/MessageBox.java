package finotek.global.dev.talkbank_ca.chat;

import java.util.ArrayList;
import java.util.List;

import rx.subjects.PublishSubject;

// Singleton Instance
public enum MessageBox {
    INSTANCE;

    public final PublishSubject<Object> observable;
    private final List<Object> messages;

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
