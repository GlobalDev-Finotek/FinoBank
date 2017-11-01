package finotek.global.dev.brazil.chat.scenario;

import android.content.Context;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.MessageBox;
import finotek.global.dev.brazil.chat.messages.ReceiveMessage;
import finotek.global.dev.brazil.chat.messages.action.Done;
import finotek.global.dev.brazil.chat.messages.control.RecommendScenarioMenuRequest;


public class ScoreCalculationScenario implements Scenario {
	private Context context;

	public ScoreCalculationScenario(Context context) {
		this.context = context;
	}

	@Override
	public boolean decideOn(String msg) {
		return msg.equals(context.getResources().getString(R.string.dialog_chat_how_score_is_computed));
	}

	@Override
	public void onReceive(Object msg) {

	}

	@Override
	public void onUserSend(String msg) {
		MessageBox.INSTANCE.addAndWait(
				new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_how_to_compute_score)),
				new RecommendScenarioMenuRequest(context),
				new Done()
		);
	}

	@Override
	public String getName() {
		return context.getResources().getString(R.string.dialog_chat_how_to_compute_score);
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean isProceeding() {
		return false;
	}
}

