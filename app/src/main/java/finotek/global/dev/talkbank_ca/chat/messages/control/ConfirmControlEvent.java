package finotek.global.dev.talkbank_ca.chat.messages.control;

import finotek.global.dev.talkbank_ca.widget.RoundButton;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfirmControlEvent {
    RoundButton.ButtonType buttonType;
    String name;
    Runnable listener;
    boolean isDisappearAfter;
}
