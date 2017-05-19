package finotek.global.dev.talkbank_ca.chat.messages.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferTo {
	String name;
	int money;
	TransactionType type;

	public enum TransactionType {
		ToSomeone, ByAI
	}
}
