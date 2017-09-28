package finotek.global.dev.talkbank_ca.chat.messages.control;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DonateItem {
    int image = 0;
    String menu = "";
    Runnable listener;
}
