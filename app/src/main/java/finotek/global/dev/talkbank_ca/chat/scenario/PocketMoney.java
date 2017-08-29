package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;
import android.util.Log;

import java.text.NumberFormat;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.context_log.ContextTotal;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SucceededMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.context.ContextScoreReceived;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.util.ContextAuthPref;
import globaldev.finotek.com.logcollector.api.score.ContextScoreResponse;

/**
 * Created by KoDeokyoon on 2017. 5. 27..
 */

public class PocketMoney implements Scenario {
	private int selectedDeposit = 0;
	private Context context;
	private Step step = Step.Initial;

	public PocketMoney(Context context) {
		this.context = context;
	}

	@Override
	public String getName() {
		return context.getString(R.string.main_string_recommend_parents_title);
	}


	@Override
	public boolean decideOn(String msg) {
		return msg.equals(R.string.main_string_recommend_parents_title) ||
				msg.equals("Parents pocket money") || msg.equals("부모님 용돈");
	}

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof ContextScoreReceived) {
			Log.d("FINOTEK", "context score received in pocket money");
			if (selectedDeposit == 0) {
				question(TransactionDB.INSTANCE.getMainBalance(), 0, ((ContextScoreReceived) msg).getScoreParams());
			} else if (selectedDeposit == 1) {
				question(TransactionDB.INSTANCE.getFirstAlternativeBalance(), 1, ((ContextScoreReceived) msg).getScoreParams());
			} else if (selectedDeposit == 2) {
				question(TransactionDB.INSTANCE.getSecondAlternativeBalance(), 2, ((ContextScoreReceived) msg).getScoreParams());
			} else if (selectedDeposit == 3) {
				question(TransactionDB.INSTANCE.getThirdAlternativeBalance(), 3, ((ContextScoreReceived) msg).getScoreParams());
			} else if (selectedDeposit == -1) {
				MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.main_string_recommend_parents_cancle)),
						new RecommendScenarioMenuRequest(context)
				);
			}
		}

		if (msg instanceof Done) {
			MessageBox.INSTANCE.addAndWait(new SendMessage(context.getResources().getString(R.string.main_string_open_account)));
			step = Step.Initial;
			selectedDeposit = 0;
		}
	}

	public RecoMenuRequest getRequestConfirm() {
		RecoMenuRequest req = new RecoMenuRequest();
		//req.setTitle("추천메뉴");
		req.setDescription(context.getResources().getString(R.string.main_string_recommend_parents_ask));

		req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_recommend_parents_yes), null);
		req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.main_string_recommend_parents_no), null);
		return req;
	}

	public RecoMenuRequest askBank() {
		RecoMenuRequest req = new RecoMenuRequest();
		req.setDescription(context.getResources().getString(R.string.dialog_chat_select_bank_select));
		req.addMenu(R.drawable.icon_speak, context.getResources().getString(R.string.dialog_chat_bank_select_main), null);
		req.addMenu(R.drawable.icon_love, context.getResources().getString(R.string.dialog_chat_bank_select_A1), null);
		req.addMenu(R.drawable.icon_mike, context.getResources().getString(R.string.dialog_chat_bank_select_A2), null);
		req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_bank_select_A3), null);
		req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_bank_select_cancel), null);
		return req;
	}

	public void question(int balance, int deposit, ContextScoreResponse scoreParams) {
		ContextAuthPref pref = new ContextAuthPref(context);
		pref.save(scoreParams);

		String message;
		if (balance < 300000)
			message = context.getResources().getString(
					R.string.main_string_recommend_parents_fail);
		else {
			if (deposit == 0) {
				TransactionDB.INSTANCE.deposit(-300000);
				balance = TransactionDB.INSTANCE.getMainBalance();
			} else if (deposit == 1) {
				TransactionDB.INSTANCE.depositV1(-300000);
				balance = TransactionDB.INSTANCE.getFirstAlternativeBalance();
			} else if (deposit == 2) {
				TransactionDB.INSTANCE.depositV2(-300000);
				balance = TransactionDB.INSTANCE.getSecondAlternativeBalance();
			} else if (deposit == 3) {
				TransactionDB.INSTANCE.depositV3(-300000);
				balance = TransactionDB.INSTANCE.getThirdAlternativeBalance();
			}

			message = context.getResources().getString(
                R.string.main_string_recommend_parents_success,
                NumberFormat.getInstance().format(51490),
                NumberFormat.getInstance().format(balance),
                pref.getTotalScore()
            );
		}

		MessageBox.INSTANCE.addAndWait(
			new SucceededMessage(context.getResources().getString(R.string.contextlog_authentication_succeeded, "이도현", 85.2)),
			new ReceiveMessage(message),
			new RecommendScenarioMenuRequest(context)
		);
	}

	@Override
	public void onUserSend(String msg) {
		LeftScenario.scenarioList.remove("P");
		switch (step) {
			case Initial:
				MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.main_string_recommend_parents_intro)),
						getRequestConfirm()
				);
				step = Step.question;
				break;
			case question:
				if (msg.equals(context.getResources().getString(R.string.main_string_recommend_parents_yes))) {
					MessageBox.INSTANCE.addAndWait(
							askBank()
					);
					step = Step.bank;
				} else if (msg.equals(context.getResources().getString(R.string.main_string_recommend_parents_no))) {

					MessageBox.INSTANCE.addAndWait(
							//new Done(),
							new ReceiveMessage(context.getResources().getString(R.string.main_string_recommend_parents_cancle)),
							new RecommendScenarioMenuRequest(context)
					);
				}
				break;
			case bank:
				if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_main))) {
					selectedDeposit = 0;
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_A1))) {
					selectedDeposit = 1;
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_A2))) {
					selectedDeposit = 2;
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_A3))) {
					selectedDeposit = 3;
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_cancel))) {
					selectedDeposit = -1;
				}
				MessageBox.INSTANCE.add(new ContextTotal());
				break;
		}
	}


	@Override
	public void clear() {

	}

	@Override
	public boolean isProceeding() {
		return false;
	}

	private enum Step {
		Initial, question, bank, onlyTransfer, openTransfer, againPicture
	}
}

