package finotek.global.dev.talkbank_ca.chat.messages;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountList {
	List<Account> list;
}
