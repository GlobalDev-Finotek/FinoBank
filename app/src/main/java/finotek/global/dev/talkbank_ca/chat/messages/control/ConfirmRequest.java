package finotek.global.dev.talkbank_ca.chat.messages.control;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.widget.RoundButton.ButtonType;
import lombok.Data;

@Data
public class ConfirmRequest {
	List<ConfirmControlEvent> events;
	Runnable doAfterEvent;

	public ConfirmRequest() {
		events = new ArrayList<>();
	}

	public static ConfirmRequest buildYesOrNo(Context context) {
		ConfirmRequest req = new ConfirmRequest();
		req.addDangerEvent(context.getResources().getString(R.string.dialog_button_no), () -> {
			MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_button_no)));
		}, true, R.drawable.icon_x);
		req.addPrimaryEvent(context.getResources().getString(R.string.dialog_button_yes), () -> {
			MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_button_yes)));
		}, true, R.drawable.icon_ok);
		return req;
	}

	public void addPrimaryEvent(String buttonName, Runnable listener, boolean willDisappearAfterFinished, int icon) {
		ConfirmControlEvent e = new ConfirmControlEvent(ButtonType.Primary, buttonName, listener, willDisappearAfterFinished);
		e.setIcon(icon);
		events.add(e);
	}

	public void addPrimaryEvent(String buttonName, Runnable listener, boolean willDisappearAfterFinished) {
		events.add(new ConfirmControlEvent(ButtonType.Primary, buttonName, listener, willDisappearAfterFinished));
	}

	public void addPrimaryEvent(String buttonName, Runnable listener) {
		addPrimaryEvent(buttonName, listener, true);
	}

	public void addInfoEvent(String buttonName, Runnable listener, boolean willDisappearAfterFinished, int icon) {
		ConfirmControlEvent e = new ConfirmControlEvent(ButtonType.Info, buttonName, listener, willDisappearAfterFinished);
		e.setIcon(icon);
		events.add(e);
	}

	public void addInfoEvent(String buttonName, Runnable listener, boolean willDisappearAfterFinished) {
		events.add(new ConfirmControlEvent(ButtonType.Info, buttonName, listener, willDisappearAfterFinished));
	}

	public void addInfoEvent(String buttonName, Runnable listener) {
		addInfoEvent(buttonName, listener, true);
	}

	public void addDangerEvent(String buttonName, Runnable listener, boolean willDisappearAfterFinished, int icon) {
		ConfirmControlEvent e = new ConfirmControlEvent(ButtonType.Danger, buttonName, listener, willDisappearAfterFinished);
		e.setIcon(icon);
		events.add(e);
	}

	public void addDangerEvent(String buttonName, Runnable listener, boolean willDisappearAfterFinished) {
		events.add(new ConfirmControlEvent(ButtonType.Danger, buttonName, listener, willDisappearAfterFinished));
	}

	public void addDangerEvent(String buttonName, Runnable listener) {
		addDangerEvent(buttonName, listener, true);
	}
}
