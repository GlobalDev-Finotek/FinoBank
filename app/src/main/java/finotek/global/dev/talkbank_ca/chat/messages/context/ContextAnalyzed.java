package finotek.global.dev.talkbank_ca.chat.messages.context;

import globaldev.finotek.com.logcollector.api.score.ContextScoreResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created by magyeong-ug on 2017. 8. 23..
 */
@Data
@RequiredArgsConstructor
public class ContextAnalyzed {

	private ContextScoreResponse scoreParams;

	public ContextAnalyzed(ContextScoreResponse scoreParams) {
		this.scoreParams = scoreParams;
	}
}
