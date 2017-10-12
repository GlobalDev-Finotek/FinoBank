package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import java.text.NumberFormat;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;

/**
 * Created by jungwon on 5/26/2017.
 */

public class ElectricityCharge implements Scenario {
	private Context context;
	private Step step = Step.Initial;

	public ElectricityCharge(Context context) {
		this.context = context;
	}

	@Override
	public String getName() {
		return context.getString(R.string.main_string_v2_login_pay_electricity);
	}

	@Override
	public boolean decideOn(String msg) {
		return msg.equals(R.string.main_string_recommend_electric_title) ||
				msg.equals("Payment of electricity bill") || msg.equals("전기료 납부") || msg.equals("Pay electricity");
	}

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof Done) {
			MessageBox.INSTANCE.addAndWait(
					new SendMessage(context.getResources().getString(R.string.main_string_open_account)));
			step = Step.Initial;
		}

	}

	public RecoMenuRequest getRequestConfirm() {
		RecoMenuRequest req = new RecoMenuRequest();
		req.setDescription(context.getResources().getString(R.string.main_string_recommend_electric_transfer_ask));
		req.addMenu(R.drawable.icon_speak, context.getResources().getString(R.string.dialog_chat_bank_select_main), null);
		req.addMenu(R.drawable.icon_love, context.getResources().getString(R.string.dialog_chat_bank_select_A1), null);
		req.addMenu(R.drawable.icon_mike, context.getResources().getString(R.string.dialog_chat_bank_select_A2), null);
		req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_bank_select_A3), null);
		req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_bank_select_cancel), null);
		return req;
	}

	public void question(int balance, int deposit) {
		String message;
		if (balance < 51490)
			message = context.getResources().getString(
					R.string.main_string_recommend_electric_transfer_fail);

		else {
			if (deposit == 0) {
				TransactionDB.INSTANCE.deposit(-51490);
				balance = TransactionDB.INSTANCE.getMainBalance();
			} else if (deposit == 1) {
				TransactionDB.INSTANCE.depositV1(-51490);
				balance = TransactionDB.INSTANCE.getFirstAlternativeBalance();
			} else if (deposit == 2) {
				TransactionDB.INSTANCE.depositV2(-51490);
				balance = TransactionDB.INSTANCE.getSecondAlternativeBalance();
			} else if (deposit == 3) {
				TransactionDB.INSTANCE.depositV3(-51490);
				balance = TransactionDB.INSTANCE.getThirdAlternativeBalance();
			}

			message = context.getResources().getString(
					R.string.main_string_recommend_electric_transfer_success,
					NumberFormat.getInstance().format(51490), NumberFormat.getInstance().format(balance));
		}

		MessageBox.INSTANCE.addAndWait(
				new ReceiveMessage(message),
				new RecommendScenarioMenuRequest(context)
		);
	}


	@Override
	public void onUserSend(String msg) {
		LeftScenario.scenarioList.remove("E");
		switch (step) {
			case Initial:
				MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_message)),
						new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_compare,
								NumberFormat.getInstance().format(51490), NumberFormat.getInstance().format(35460))),
						getRequestConfirm()
				);
				step = Step.question;
				break;
			case question:
				if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_main))) {
					question(TransactionDB.INSTANCE.getMainBalance(), 0);
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_A1))) {
					question(TransactionDB.INSTANCE.getFirstAlternativeBalance(), 1);
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_A2))) {
					question(TransactionDB.INSTANCE.getSecondAlternativeBalance(), 2);
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_A3))) {
					question(TransactionDB.INSTANCE.getThirdAlternativeBalance(), 3);
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_cancel))) {
					MessageBox.INSTANCE.addAndWait(
							new ReceiveMessage(context.getResources().getString(R.string.main_string_recommend_electric_transfer_cancle)),
							new RecommendScenarioMenuRequest(context)
					);
				}
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
		Initial, question, onlyTransfer, openTransfer, againPicture
	}
}
