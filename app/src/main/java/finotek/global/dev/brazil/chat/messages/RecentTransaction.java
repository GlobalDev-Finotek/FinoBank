package finotek.global.dev.brazil.chat.messages;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecentTransaction {
	List<Transaction> transactions;
}
