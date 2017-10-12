package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.Account;
import finotek.global.dev.talkbank_ca.chat.messages.AccountList;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RequestContactPermission;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SucceededMessage;
import finotek.global.dev.talkbank_ca.chat.messages.Transaction;
import finotek.global.dev.talkbank_ca.chat.messages.WarningMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.MoneyTransferred;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.contact.RequestSelectContact;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI_v1;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI_v2;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI_v3;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.alterOne;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.alterThree;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.alterTwo;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.model.DBHelper;

public class TransferScenario implements Scenario {
	private DBHelper dbHelper;
	private Context context;
	private Step step = Step.BankAsk;
	private boolean isProceeding = true;
	private int navigateNum = 0;

	public TransferScenario(Context context, DBHelper dbHelper) {
		this.context = context;
		this.dbHelper = dbHelper;
	}

	@Override
	public String getName() {
		return context.getString(R.string.scenario_transfer);
	}

	@Override
	public boolean decideOn(String msg) {
		return msg.equals("계좌이체") || msg.equals(context.getResources().getString(R.string.dialog_button_transfer))
				|| msg.equals("이체") || msg.equals("송금");
	}

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof TransferButtonPressed) {
			String name = TransactionDB.INSTANCE.getTxName();
			String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
			int money = TransactionDB.INSTANCE.getMoneyAsInt();

            MessageBox.INSTANCE.add(new RequestRemoveControls());
            MessageBox.INSTANCE.add(new SendMessage(context.getString(R.string.dialog_chat_send_transfer, name, moneyAsString)));

			if (money >= 1000000) {
				TransactionDB.INSTANCE.setTransfer(true);

				MessageBox.INSTANCE.addAndWait(
						new WarningMessage(context.getResources().getString(R.string.contextlog_authentication_waring, "이도현", 85.2)),
						new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_finger_tip_sign)),
						new RequestSignature()
				);
			} else {
				Object recvMessage = null;
				switch (navigateNum) {
					case 1:
						recvMessage = new MoneyTransferred(true);
						break;
					case 2:
						recvMessage = new alterOne(true);
						break;
					case 3:
						recvMessage = new alterTwo(true);
						break;
					case 4:
						recvMessage = new alterThree(true);
						break;
					default:
						break;
				}

				MessageBox.INSTANCE.add(recvMessage);
			}
		}

		if (msg instanceof SignatureVerified) {
			TransactionDB.INSTANCE.setTransfer(false);
			Object recvMessage = null;
			switch (navigateNum) {
				case 1:
					recvMessage = new MoneyTransferred(false);
					break;
				case 2:
					recvMessage = new alterOne(false);
					break;
				case 3:
					recvMessage = new alterTwo(false);
					break;
				case 4:
					recvMessage = new alterThree(false);
					break;
			}

			MessageBox.INSTANCE.add(recvMessage);
		}

		if (msg instanceof MoneyTransferred) {
			isProceeding = false;
			String name = TransactionDB.INSTANCE.getTxName();
			String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
			int money = TransactionDB.INSTANCE.getMoneyAsInt();

			TransactionDB.INSTANCE.transferMoney(money);
			int balance = TransactionDB.INSTANCE.getMainBalance();
			String balanceAsString = NumberFormat.getNumberInstance().format(balance);
			TransactionDB.INSTANCE.addTx(new Transaction(name, 0, money, balance, new DateTime()));

			RecommendScenarioMenuRequest request = new RecommendScenarioMenuRequest(context);
			request.setTitle("");
			request.setDescription(context.getString(R.string.dialog_chat_after_transfer, name, moneyAsString, balanceAsString));

			if (((MoneyTransferred) msg).isAuthenticated()) {
				MessageBox.INSTANCE.addAndWait(
						new SucceededMessage(context.getResources().getString(R.string.contextlog_authentication_succeeded, "이도현", 85.2)),
						request,
						new Done()
				);
			} else {
				MessageBox.INSTANCE.addAndWait(request, new Done());
			}
			step = Step.TransferDone;
		}
		if (msg instanceof alterOne) {
			isProceeding = false;
			String name = TransactionDB.INSTANCE.getTxName();
			String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
			int money = TransactionDB.INSTANCE.getMoneyAsInt();

			TransactionDB.INSTANCE.transferMoneyV1(money);
			int balance = TransactionDB.INSTANCE.getFirstAlternativeBalance();

			String balanceAsString = NumberFormat.getNumberInstance().format(balance);
			TransactionDB.INSTANCE.addTx(new Transaction(name, 0, money, balance, new DateTime()));

			RecommendScenarioMenuRequest request = new RecommendScenarioMenuRequest(context);
			request.setTitle("");
			request.setDescription(context.getString(R.string.dialog_chat_after_transfer, name, moneyAsString, balanceAsString));

			if (((alterOne) msg).isAuthenticated()) {
				MessageBox.INSTANCE.addAndWait(
						new SucceededMessage(context.getResources().getString(R.string.contextlog_authentication_succeeded, "이도현", 85.2)),
						request,
						new Done()
				);
			} else {
				MessageBox.INSTANCE.addAndWait(request, new Done());
			}
			step = Step.TransferDone;
		}
		if (msg instanceof alterTwo) {
			isProceeding = false;
			String name = TransactionDB.INSTANCE.getTxName();
			String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
			int money = TransactionDB.INSTANCE.getMoneyAsInt();

			TransactionDB.INSTANCE.transferMoneyV2(money);
			int balance = TransactionDB.INSTANCE.getSecondAlternativeBalance();

			String balanceAsString = NumberFormat.getNumberInstance().format(balance);
			TransactionDB.INSTANCE.addTx(new Transaction(name, 0, money, balance, new DateTime()));

			RecommendScenarioMenuRequest request = new RecommendScenarioMenuRequest(context);
			request.setTitle("");
			request.setDescription(context.getString(R.string.dialog_chat_after_transfer, name, moneyAsString, balanceAsString));

			if (((alterTwo) msg).isAuthenticated()) {
				MessageBox.INSTANCE.addAndWait(
						new SucceededMessage(context.getResources().getString(R.string.contextlog_authentication_succeeded, "이도현", 85.2)),
						request,
						new Done()
				);
			} else {
				MessageBox.INSTANCE.addAndWait(request, new Done());
			}
			step = Step.TransferDone;
		}

		if (msg instanceof alterThree) {
			isProceeding = false;
			String name = TransactionDB.INSTANCE.getTxName();
			String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
			int money = TransactionDB.INSTANCE.getMoneyAsInt();

			TransactionDB.INSTANCE.transferMoneyV3(money);
			int balance = TransactionDB.INSTANCE.getThirdAlternativeBalance();

			String balanceAsString = NumberFormat.getNumberInstance().format(balance);
			TransactionDB.INSTANCE.addTx(new Transaction(name, 0, money, balance, new DateTime()));

			RecommendScenarioMenuRequest request = new RecommendScenarioMenuRequest(context);
			request.setTitle("");
			request.setDescription(context.getString(R.string.dialog_chat_after_transfer, name, moneyAsString, balanceAsString));

			if (((alterThree) msg).isAuthenticated()) {
				MessageBox.INSTANCE.addAndWait(
						new SucceededMessage(context.getResources().getString(R.string.contextlog_authentication_succeeded, "이도현", 85.2)),
						request,
						new Done()
				);
			} else {
				MessageBox.INSTANCE.addAndWait(request, new Done());
			}
			step = Step.TransferDone;
		}
		if (msg instanceof Done) {
			this.clear();
		}


	}

	public void confirmBankSelection(int balance) {
		RecoMenuRequest req = new RecoMenuRequest();
		req.setDescription(context.getResources().getString(R.string.dialog_chat_bank_balance, balance));
		req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_select_bank_yes), null);
		req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_select_bank_no), null);
		MessageBox.INSTANCE.addAndWait(
				req
		);
		step = Step.BankChoice;
	}

	@Override
	public void onUserSend(String msg) {
		RecoMenuRequest req = new RecoMenuRequest();
		req.setDescription(context.getResources().getString(R.string.dialog_chat_before_transfer));
		req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_select_bank_yes), null);
		req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_select_bank_no), null);
		switch (step) {
			case BankAsk:

				MessageBox.INSTANCE.addAndWait(
						req
				);
				step = Step.BankAnswer;
				break;

			case BankAnswer:
				if (msg.equals(context.getResources().getString(R.string.dialog_chat_select_bank_yes))) {
					Accounts(1);
				} else if ((msg.equals(context.getResources().getString(R.string.dialog_chat_select_bank_no)))) {
					RecoMenuRequest selectbank = new RecoMenuRequest();
					selectbank.setDescription(context.getResources().getString(R.string.dialog_chat_bank_select));
					selectbank.addMenu(R.drawable.icon_love, context.getResources().getString(R.string.dialog_chat_bank_select_A1), null);
					selectbank.addMenu(R.drawable.icon_mike, context.getResources().getString(R.string.dialog_chat_bank_select_A2), null);
					selectbank.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_bank_select_A3), null);
					selectbank.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_bank_select_cancel), null);

					MessageBox.INSTANCE.addAndWait(
							selectbank
					);
					step = Step.Question;
					break;
				}
			case Question:
				if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_A1))) {
					confirmBankSelection(TransactionDB.INSTANCE.getFirstAlternativeBalance());
					this.navigateNum = 2;
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_A2))) {
					confirmBankSelection(TransactionDB.INSTANCE.getSecondAlternativeBalance());
					this.navigateNum = 3;
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_A3))) {
					confirmBankSelection(TransactionDB.INSTANCE.getThirdAlternativeBalance());
					this.navigateNum = 4;
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_bank_select_cancel))) {
					MessageBox.INSTANCE.addAndWait(new ReceiveMessage(
									context.getResources().getString(R.string.dialog_chat_transfer_cancel)),
							new RecommendScenarioMenuRequest(context)
					);
				}
				break;

			case BankChoice:
				if (msg.equals(context.getResources().getString(R.string.dialog_chat_select_bank_yes))) {
					Accounts(navigateNum);
				} else if (msg.equals(context.getResources().getString(R.string.dialog_chat_select_bank_no))) {
					RecoMenuRequest selectbank = new RecoMenuRequest();
					selectbank.setDescription(context.getResources().getString(R.string.dialog_chat_bank_select));
					selectbank.addMenu(R.drawable.icon_love, context.getResources().getString(R.string.dialog_chat_bank_select_A1), null);
					selectbank.addMenu(R.drawable.icon_mike, context.getResources().getString(R.string.dialog_chat_bank_select_A2), null);
					selectbank.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_bank_select_A3), null);
					selectbank.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_bank_select_cancel), null);

					MessageBox.INSTANCE.addAndWait(
							selectbank
					);
				}
				break;
		}

	}

	@Override
	public void clear() {
		isProceeding = true;
		step = Step.BankAsk;
		TransactionDB.INSTANCE.setTxMoney("");
		TransactionDB.INSTANCE.setTxName("");
	}

	@Override
	public boolean isProceeding() {
		return isProceeding;
	}

	private void Accounts(int navigateNum) {
		this.navigateNum = navigateNum;
		List<Account> accounts = new ArrayList<>();
		accounts.add(new Account("어머니", "2017/01/25", "200,000 원 " + context.getString(R.string.string_transfer).toLowerCase(), true));
		accounts.add(new Account("박예린", "2017/01/11", "100,000 원 " + context.getString(R.string.string_transfer).toLowerCase(), false));
		accounts.add(new Account("김가람", "2017/01/11", "36,200 원 " + context.getString(R.string.string_transfer).toLowerCase(), false));
		accounts.add(new Account("김이솔", "2017/01/10", "100,000 원 " + context.getString(R.string.dialog_string_deposit).toLowerCase(), false));

		ConfirmRequest confirmRequest = new ConfirmRequest();
		confirmRequest.addInfoEvent(context.getString(R.string.dialog_contact), () -> {
			MessageBox.INSTANCE.add(new RequestSelectContact());
		}, false);

		if (navigateNum == 1) {
			MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage(context.getString(R.string.dialog_string_select_receiver)),
					new AccountList(accounts),
					confirmRequest,
					new RequestTransferUI(),
					new RequestContactPermission()
			);
		} else if (navigateNum == 2) {
			MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage(context.getString(R.string.dialog_string_select_receiver)),
					new AccountList(accounts),
					confirmRequest,
					new RequestTransferUI_v1(),
					new RequestContactPermission()
			);
		} else if (navigateNum == 3) {
			MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage(context.getString(R.string.dialog_string_select_receiver)),
					new AccountList(accounts),
					confirmRequest,
					new RequestTransferUI_v2(),
					new RequestContactPermission()
			);
		} else if (navigateNum == 4) {
			MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage(context.getString(R.string.dialog_string_select_receiver)),
					new AccountList(accounts),
					confirmRequest,
					new RequestTransferUI_v3(),
					new RequestContactPermission()
			);
		}
	}


	private enum Step {
		BankAsk, BankAnswer, Question, BankChoice, Initial, TransferToSomeone, TransferByAI, TransferDone
	}
}