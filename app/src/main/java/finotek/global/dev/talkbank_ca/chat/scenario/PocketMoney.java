package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import java.text.NumberFormat;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.WarningMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignatureRegister;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignatureValidation;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;

/**
 * Created by KoDeokyoon on 2017. 5. 27..
 */

public class PocketMoney implements Scenario {
	private int account = -1;
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
		if (msg instanceof Done) {
			step = Step.Initial;
			account = -1;
		}

		if (msg instanceof SignatureVerified) {
			TransactionDB.INSTANCE.setTransfer(false);

			if (account == 1) {
				question(TransactionDB.INSTANCE.getMainBalance(), 0);
			} else if (account == 2) {
				question(TransactionDB.INSTANCE.getFirstAlternativeBalance(), 1);
			} else if (account == 3) {
				question(TransactionDB.INSTANCE.getSecondAlternativeBalance(), 2);
			} else if (account == 4) {
				question(TransactionDB.INSTANCE.getThirdAlternativeBalance(), 3);
			} else {
				MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.main_string_recommend_parents_cancle)),
						new RecommendScenarioMenuRequest(context),
						new Done()
				);
			}
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
		req.addMenu(R.drawable.icon_speak, context.getResources().getString(R.string.dialog_chat_bank_select_main), () -> {
			account = 1;
			MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_chat_bank_select_main)));
		});
		req.addMenu(R.drawable.icon_love, context.getResources().getString(R.string.dialog_chat_bank_select_A1), () -> {
			account = 2;
			MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_chat_bank_select_A1)));
		});
		req.addMenu(R.drawable.icon_mike, context.getResources().getString(R.string.dialog_chat_bank_select_A2), () -> {
			account = 3;
			MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_chat_bank_select_A2)));
		});
		req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_bank_select_A3), () -> {
			account = 4;
			MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_chat_bank_select_A3)));
		});
		req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_bank_select_cancel), () -> {
			account = -1;
			MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_chat_bank_select_cancel)));
		});
		return req;
	}

	public void question(int balance, int deposit) {
		String message;
		if (balance < 1200000)
			message = context.getResources().getString(
					R.string.main_string_recommend_parents_fail);
		else {
			if (deposit == 0) {
				TransactionDB.INSTANCE.deposit(-1200000);
				balance = TransactionDB.INSTANCE.getMainBalance();
			} else if (deposit == 1) {
				TransactionDB.INSTANCE.depositV1(-1200000);
				balance = TransactionDB.INSTANCE.getFirstAlternativeBalance();
			} else if (deposit == 2) {
				TransactionDB.INSTANCE.depositV2(-1200000);
				balance = TransactionDB.INSTANCE.getSecondAlternativeBalance();
			} else if (deposit == 3) {
				TransactionDB.INSTANCE.depositV3(-1200000);
				balance = TransactionDB.INSTANCE.getThirdAlternativeBalance();
			}

			message = context.getResources().getString(
					R.string.main_string_recommend_parents_success,
					NumberFormat.getInstance().format(1200000),
					NumberFormat.getInstance().format(balance)
			);
		}

		MessageBox.INSTANCE.addAndWait(
				new ReceiveMessage(message),
				new RecommendScenarioMenuRequest(context),
				new Done()
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
					step = Step.sign;
				} else if (msg.equals(context.getResources().getString(R.string.main_string_recommend_parents_no))) {
					MessageBox.INSTANCE.addAndWait(
							//new Done(),
							new ReceiveMessage(context.getResources().getString(R.string.main_string_recommend_parents_cancle)),
							new RecommendScenarioMenuRequest(context)
					);
				}
				break;
			case sign:
				TransactionDB.INSTANCE.setTxName("어머니");
				TransactionDB.INSTANCE.setTxMoney("1,200,000");
				TransactionDB.INSTANCE.setTransfer(true);

				MessageBox.INSTANCE.addAndWait(
						new WarningMessage(context.getResources().getString(R.string.contextlog_authentication_needed)),
						new RequestSignatureValidation()
				);
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
		Initial, question, sign, bank, onlyTransfer, openTransfer, againPicture
	}
}

