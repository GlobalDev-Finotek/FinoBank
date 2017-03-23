package finotek.global.dev.talkbank_ca.model;

import finotek.global.dev.talkbank_ca.chat.adapter.ChatItem;
import lombok.Data;
import rx.functions.Action1;

@Data
public class ChatSelectButtonEvent extends ChatItem {
    Action1<Void> confirmAction;
    Action1<Void> cancelAction;
}
