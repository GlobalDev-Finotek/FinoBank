package finotek.global.dev.talkbank_ca.chat.messages.control;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecoMenu {
	int icon = 0;
	String menuName = "";
	Runnable listener;
}
