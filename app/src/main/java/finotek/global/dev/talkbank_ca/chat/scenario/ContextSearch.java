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
import finotek.global.dev.talkbank_ca.chat.messages.context.ContextScoreReceived;
import finotek.global.dev.talkbank_ca.chat.messages.context.CurrentAddressReceived;
import finotek.global.dev.talkbank_ca.chat.messages.context.RequestCurrentAddress;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.util.ContextAuthPref;
import globaldev.finotek.com.logcollector.api.score.BaseScoreParam;
import globaldev.finotek.com.logcollector.api.score.ContextScoreResponse;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.util.AesInstance;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoGetter;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoGetterImpl;
import io.realm.Realm;

public class ContextSearch implements Scenario {
	private Step step = Step.initial;
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
            step = Step.initial;
            MessageBox.INSTANCE.addAndWait(new RecommendScenarioMenuRequest(context));
        }

        if(msg instanceof CurrentAddressReceived) {
			String address = ((CurrentAddressReceived) msg).getAddress();

			String message = "";
			message = buildScoreMessages(address);
			MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_contextlog_result_message, message)), new Done());
			Log.d("FINOTEK", "search message ended");
		}
	}

	@Override
	public void onUserSend(String msg) {
		switch (step) {
            case initial:
                MessageBox.INSTANCE.add(new RequestCurrentAddress());
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

	private String buildScoreMessages(String address) {
		String messages = "";
		messages += "1: 당신은 2분전 서중원(친밀도: 12위)과(와) 5분간 통화한 것이 확인되었으므로 65점을 얻었습니다.\n\n";
		messages += "2: 당신은 24분전 서중원(친밀도: 12위)과(와) 3분간 통화한 것이 확인되었으므로 35점을 얻었습니다.\n\n";
		messages += "3: 당신은 5분전 카카오톡 앱(친밀도: 1위)을(를) 사용한 것이 확인되었으므로 88점을 얻었습니다.\n\n";
		messages += "4: 당신은 28분전 프로젝트웨어 앱(친밀도: 7위)을(를) 사용한 것이 확인되었으므로 22점을 얻었습니다.\n\n";
		messages += "5: 당신은 " + address + "(친밀도: 100+위)에 있는 것이 확인되었으므로 73점을 얻었습니다.\n\n";
		messages += "6: 당신은 10분전 이도현(친밀도: 15위)과(와) 메시지를 주고받은 확인되었으므로 34점을 얻었습니다.";

		return messages;
	}

	private enum Step {
		initial, ask
	}
}
