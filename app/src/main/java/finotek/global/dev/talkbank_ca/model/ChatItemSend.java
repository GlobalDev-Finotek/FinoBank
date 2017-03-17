package finotek.global.dev.talkbank_ca.model;

import finotek.global.dev.talkbank_ca.chat.adapter.ChatItem;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatItemSend extends ChatItem {
    private String message;
}
