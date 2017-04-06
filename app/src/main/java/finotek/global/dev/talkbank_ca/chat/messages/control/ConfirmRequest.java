package finotek.global.dev.talkbank_ca.chat.messages.control;

import android.content.res.Resources;

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

    public void addPrimaryEvent(String buttonName, Runnable listener){
        events.add(new ConfirmControlEvent(ButtonType.Primary, buttonName, listener));
    }

    public void addInfoEvent(String buttonName, Runnable listener){
        events.add(new ConfirmControlEvent(ButtonType.Info, buttonName, listener));
    }

    public void addDangerEvent(String buttonName, Runnable listener){
        events.add(new ConfirmControlEvent(ButtonType.Danger, buttonName, listener));
    }

    public static ConfirmRequest buildYesOrNo(){
        ConfirmRequest req = new ConfirmRequest();
        req.addDangerEvent(Resources.getSystem().getString(R.string.dialog_button_no), () -> {
            MessageBox.INSTANCE.add(new SendMessage(Resources.getSystem().getString(R.string.dialog_button_no)));
        });
        req.addPrimaryEvent(Resources.getSystem().getString(R.string.dialog_button_yes), () -> {
            MessageBox.INSTANCE.add(new SendMessage(Resources.getSystem().getString(R.string.dialog_button_yes)));
        });
        return req;
    }
}
