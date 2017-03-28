package finotek.global.dev.talkbank_ca.chat.messages;

import java.util.List;

import lombok.Data;

@Data
public class RecentTransaction {
    List<Transaction> transactions;
}
