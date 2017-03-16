package finotek.global.dev.talkbank_ca.model;

import java.util.Date;

import finotek.global.dev.talkbank_ca.chat.adapter.ChatItem;
import lombok.Data;

@Data
public class ChatItemDivider extends ChatItem {
    Date date;
}
