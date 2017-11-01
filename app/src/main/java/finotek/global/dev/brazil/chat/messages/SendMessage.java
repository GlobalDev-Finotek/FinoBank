package finotek.global.dev.brazil.chat.messages;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SendMessage {
	@NonNull
	String message;
	int icon = -1;
	boolean onlyDisplay = false;

	public SendMessage(String message, int icon) {
		this.message = message;
		this.icon = icon;
	}
}
