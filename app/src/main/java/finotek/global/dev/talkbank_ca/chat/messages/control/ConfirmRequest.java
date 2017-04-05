package finotek.global.dev.talkbank_ca.chat.messages.control;

import java.util.ArrayList;
import java.util.List;

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
        req.addDangerEvent("아니오", () -> {
            MessageBox.INSTANCE.add(new SendMessage("아니오"));
        });
        req.addPrimaryEvent("네", () -> {
            MessageBox.INSTANCE.add(new SendMessage("네"));
        });
        return req;
    }
}
