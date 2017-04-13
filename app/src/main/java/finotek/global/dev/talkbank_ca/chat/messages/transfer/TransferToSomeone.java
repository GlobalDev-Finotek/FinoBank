package finotek.global.dev.talkbank_ca.chat.messages.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferToSomeone {
    String name;
    int money;
}
