package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;
import android.util.Log;

import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.context_log.ContextApp;
import finotek.global.dev.talkbank_ca.chat.context_log.ContextCall;
import finotek.global.dev.talkbank_ca.chat.context_log.ContextLocation;
import finotek.global.dev.talkbank_ca.chat.context_log.ContextSms;
import finotek.global.dev.talkbank_ca.chat.context_log.ContextTotal;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;

/**
 * Created by jungwon on 7/31/2017.
 */

public class ContextSearch implements Scenario {
	private Step step = Step.question;
	private Context context;

	public ContextSearch(Context context) {
		this.context = context;
	}

	@Override
	public boolean decideOn(String msg) {
		return msg.equals("맥락") || msg.equals("ㅁㄹ") || msg.equals(context.getResources().getString(R.string.dialog_chat_contextlog_search));
	}

	@Override
	public void onReceive(Object msg) {
		if(msg instanceof Done) {
            step = Step.question;
            MessageBox.INSTANCE.addAndWait(new RecommendScenarioMenuRequest(context));
        }
	}

	@Override
	public void onUserSend(String msg) {
		switch (step) {
            case question:
				MessageBox.INSTANCE.addAndWait(buildRecommendedMenu());
				step = Step.ask;
				break;
			case ask:
				if (msg.equals(context.getResources().getString(R.string.dialog_chat_contextlog_total))) {
					MessageBox.INSTANCE.addAndWait(
							new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_contextlog_result)),
							new ContextTotal()
					);
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_contextlog_sms))) {
					MessageBox.INSTANCE.addAndWait(
							new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_contextlog_result)),
							new ContextSms()
					);
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_contextlog_call))) {
					MessageBox.INSTANCE.addAndWait(
							new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_contextlog_result)),
							new ContextCall()
					);
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_contextlog_location))) {
					MessageBox.INSTANCE.addAndWait(
							new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_contextlog_result)),
							new ContextLocation()
					);
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_contextlog_application))) {
					MessageBox.INSTANCE.addAndWait(
							new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_contextlog_result)),
							new ContextApp()
					);
				}
				break;
		}
	}

	@Override
	public String getName() {
		return context.getResources().getString(R.string.dialog_chat_contextlog_search);
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean isProceeding() {
		return false;
	}

	private RecoMenuRequest buildRecommendedMenu(){
        String initMessage = context.getResources().getString(R.string.dialog_chat_contextlog_search_initMessage);
        RecoMenuRequest req = new RecoMenuRequest();
        req.setDescription(initMessage);
        req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.dialog_chat_contextlog_total), null);
        req.addMenu(R.drawable.icon_bookmark_selected, context.getResources().getString(R.string.dialog_chat_contextlog_sms), null);
        req.addMenu(R.drawable.icon_mike, context.getResources().getString(R.string.dialog_chat_contextlog_call), null);
        req.addMenu(R.drawable.icon_camera, context.getResources().getString(R.string.dialog_chat_contextlog_location), null);
        req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_contextlog_application), null);

        return req;
    }

	private enum Step {
		question, ask
	}
}
