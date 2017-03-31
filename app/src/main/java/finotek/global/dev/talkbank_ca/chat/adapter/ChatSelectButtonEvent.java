package finotek.global.dev.talkbank_ca.chat.adapter;

import lombok.Data;
import rx.functions.Action1;

@Data
public class ChatSelectButtonEvent {
    Action1<Void> confirmAction;
    Action1<Void> cancelAction;
}
