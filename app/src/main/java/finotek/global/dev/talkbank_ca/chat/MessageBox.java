package finotek.global.dev.talkbank_ca.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.chat.messages.MessageEmitted;
import finotek.global.dev.talkbank_ca.chat.messages.WaitForMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.EnableToEditMoney;
import finotek.global.dev.talkbank_ca.chat.messages.contact.SelectedContact;
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

		if(!(msg instanceof EnableToEditMoney) && !(msg instanceof SelectedContact)) {
			observable.onNext(new WaitForMessage());
		}

		observable.onNext(msg);
	}

	public void removeAt(int index){
		messages.remove(index);
	}

	public int size() {
		return messages.size();
	}
}