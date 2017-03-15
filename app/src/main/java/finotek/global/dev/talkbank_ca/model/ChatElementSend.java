package finotek.global.dev.talkbank_ca.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatElementSend extends ChatElement {
    private String name;
    private String message;
}
