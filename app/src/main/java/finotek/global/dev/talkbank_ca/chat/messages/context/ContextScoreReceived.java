package finotek.global.dev.talkbank_ca.chat.messages.context;

import globaldev.finotek.com.logcollector.api.score.ContextScoreResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by scal on 2017. 8. 29..
 */

@Data
@AllArgsConstructor
public class ContextScoreReceived {
	public ContextScoreResponse scoreParams;
}
