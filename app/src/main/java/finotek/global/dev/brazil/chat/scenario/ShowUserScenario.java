package finotek.global.dev.brazil.chat.scenario;

import android.content.Context;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.MessageBox;
import finotek.global.dev.brazil.chat.messages.ReceiveMessage;
import finotek.global.dev.brazil.chat.messages.action.Done;
import finotek.global.dev.brazil.chat.messages.action.SignatureVerified;
import finotek.global.dev.brazil.chat.messages.ui.RequestSignatureRegister;

/**
 * Created by idohyeon on 2017. 10. 26..
 */

public class ShowUserScenario implements Scenario {
	private Step step = ShowUserScenario.Step.initial;
	private Context context;
	private String yes = "";
	private String no = "";

	public ShowUserScenario(Context context) {
		this.context = context;
		yes = context.getString(R.string.string_yes);
		no = context.getString(R.string.string_no);
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean decideOn(String msg) {
		return msg.equals("dddd");
	}

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof SignatureVerified) {
			MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage("É correto sua assinatura escondida. A verificação está completa."),
					new Done()
			);
		}
	}

	@Override
	public void onUserSend(String msg) {

		switch (step) {
			case initial:
				if (msg.equals(yes)) {
					MessageBox.INSTANCE.addAndWait(
							new RequestSignatureRegister()
					);
				}

				break;
			case choice:

				break;
		}

	}

	@Override
	public void clear() {

	}

	@Override
	public boolean isProceeding() {
		return true;
	}

	private enum Step {
		initial, choice
	}
}