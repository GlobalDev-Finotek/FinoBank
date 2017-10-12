package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;
import android.util.Log;

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
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.util.ContextAuthPref;
import globaldev.finotek.com.logcollector.api.score.BaseScoreParam;
import globaldev.finotek.com.logcollector.api.score.ContextScoreResponse;
import globaldev.finotek.com.logcollector.model.ActionType;
import io.realm.Realm;

public class ContextSearch implements Scenario {
	private Step step = Step.question;
	private Context context;

	public ContextSearch(Context context) {
		this.context = context;
	}

	@Override
	public boolean decideOn(String msg) {
		return msg.equals("맥락") || msg.equals("ㅁㄹ") || msg.equals(context.getResources().getString(R.string.dialog_chat_contextlog_search)) || msg.equals("맥락점수상세조회");
	}

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof Done) {
			step = Step.question;
			MessageBox.INSTANCE.addAndWait(new RecommendScenarioMenuRequest(context));
		}

		if (msg instanceof ContextScoreReceived) {
			String message = "";
			ContextScoreResponse scoreParams = ((ContextScoreReceived) msg).getScoreParams();
			if (scoreParams.messages == null || scoreParams.messages.size() == 0) {
				message = context.getResources().getString(R.string.dialog_chat_contextlog_result_nothing);
			} else {
				message = buildScoreMessages(scoreParams);
			}

			MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_contextlog_result_message, message)), new Done());
		}
	}

	@Override
	public void onUserSend(String msg) {
		switch (step) {
			case question:
				Log.d("FINOTEK", "search message started");
				ContextAuthPref pref = new ContextAuthPref(context);
				String message = "";
				ContextScoreResponse scoreParams = pref.getScoreParams();
				Log.d("FINOTEK", "search message started.." + scoreParams);

				if (scoreParams == null || scoreParams.messages == null || scoreParams.messages.size() == 0) {
					message = context.getResources().getString(R.string.dialog_chat_contextlog_result_nothing);
				} else {
					message = buildScoreMessages(scoreParams);
				}

				MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_contextlog_result_message, message)), new Done());
//				MessageBox.INSTANCE.addAndWait(buildRecommendedMenu());
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

	private RecoMenuRequest buildRecommendedMenu() {
		ContextAuthPref pref = new ContextAuthPref(context);
		float smsScore = pref.getMessageScore();
		float appUsageScore = pref.getAppUsageScore();
		float callScore = pref.getCallScore();
		float locationScore = pref.getLocationScore();
		float total = pref.getTotalScore();

		Realm realm = Realm.getDefaultInstance();
		User user = realm.where(User.class).findAll().last();
		String initMessage = context.getResources().getString(R.string.dialog_chat_contextlog_search_initMessage, user.getName(), total, callScore, smsScore, locationScore, appUsageScore);
		RecoMenuRequest req = new RecoMenuRequest();
		req.setDescription(initMessage);
		req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.dialog_chat_contextlog_total), null);
//        req.addMenu(R.drawable.icon_bookmark_selected, context.getResources().getString(R.string.dialog_chat_contextlog_sms), null);
//        req.addMenu(R.drawable.icon_mike, context.getResources().getString(R.string.dialog_chat_contextlog_call), null);
//        req.addMenu(R.drawable.icon_camera, context.getResources().getString(R.string.dialog_chat_contextlog_location), null);
//        req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_contextlog_application), null);

		return req;
	}

	private String buildScoreMessages(ContextScoreResponse scoreResponse) {
		String messages = "";
		int size = scoreResponse.messages.size();
		for (int i = 0; i < size; i++) {
			BaseScoreParam msg = scoreResponse.messages.get(i);
			Log.d("FINOPASS", msg.toString());

			switch (msg.type) {
				case ActionType.GATHER_APP_USAGE_LOG:
					String appName = msg.param.get("appName");
					messages += (i + 1) + ": " + context.getResources().getString(R.string.contextlog_result_message_app_usage, appName, msg.rank, msg.beforeTime, msg.score);
					break;
				case ActionType.GATHER_CALL_LOG: {
					String targetName = msg.param.get("targetName");
					if (targetName == null || targetName.isEmpty() || targetName.equals(" "))
						targetName = "아무개";

					messages += (i + 1) + ": " + context.getResources().getString(R.string.contextlog_result_phone_call, targetName, msg.rank, msg.beforeTime, msg.score);
				}
				break;
				case ActionType.GATHER_MESSAGE_LOG: {
					String targetNumber = msg.param.get("targetNumber");
					if (targetNumber == null || targetNumber.isEmpty() || targetNumber.equals("null"))
						targetNumber = "아무개";

					messages += (i + 1) + ": " + context.getResources().getString(R.string.contextlog_result_message, targetNumber, msg.rank, msg.beforeTime, msg.score);
				}
				break;
				case ActionType.GATHER_LOCATION_LOG:
					messages += (i + 1) + ": " + context.getResources().getString(R.string.contextlog_result_location, Float.valueOf(msg.param.get("latitude")), Float.valueOf(msg.param.get("longitude")), msg.rank, msg.score);
					break;
			}

			if (i != size - 1)
				messages += "\n\n";
		}

		return messages;
	}

	private enum Step {
		question, ask
	}
}