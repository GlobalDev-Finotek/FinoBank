package finotek.global.dev.talkbank_ca.chat.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SendMessage {
    @NonNull
    String message;
    int icon = -1;

    public SendMessage(String message, int icon) {
        this.message = message;
        this.icon = icon;
    }
}
