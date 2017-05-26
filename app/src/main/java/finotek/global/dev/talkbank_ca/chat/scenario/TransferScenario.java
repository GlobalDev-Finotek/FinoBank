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
import finotek.global.dev.talkbank_ca.chat.messages.RecentTransaction;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.Transaction;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.MoneyTransferred;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.contact.RequestSelectContact;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferTo;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.model.DBHelper;
import finotek.global.dev.talkbank_ca.model.User;
import io.realm.Realm;

public class TransferScenario implements Scenario {
	private DBHelper dbHelper;
	private Context context;
	private Step step = Step.Initial;
	private boolean isProceeding = true;

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
		if (msg instanceof TransferTo) {
			TransferTo action = (TransferTo) msg;
			String name = action.getName();
			String money = NumberFormat.getNumberInstance().format(action.getMoney());

			Realm realm = Realm.getDefaultInstance();
			User user = realm.where(User.class).findAll().last();

			String message = "";
			if (action.getType() == TransferTo.TransactionType.ToSomeone) {
				message = context.getResources().getString(R.string.dialog_chat_transfer_to_someone, name, money);
				step = Step.TransferToSomeone;
			} else {
				DateTime dateTime = new DateTime();
				int day = dateTime.getDayOfMonth();
				message = context.getResources().getString(R.string.dialog_chat_transaction, user.getName(), money, day);
				step = Step.TransferByAI;
			}

			ReceiveMessage msgObject = new ReceiveMessage(message);
			TransactionDB.INSTANCE.setTxName(name);
			TransactionDB.INSTANCE.setTxMoney(money);
			ConfirmRequest request = ConfirmRequest.buildYesOrNo(context);
			request.addInfoEvent(context.getResources().getString(R.string.dialog_button_transfer_other), () -> {
				MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_button_transfer_other)));
			});
			MessageBox.INSTANCE.addAndWait(msgObject, request);
		}

		if (msg instanceof SignatureVerified) {
			MessageBox.INSTANCE.add(new MoneyTransferred());
		}

		if (msg instanceof MoneyTransferred) {
			isProceeding = false;
			String name = TransactionDB.INSTANCE.getTxName();
			String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
			int money = TransactionDB.INSTANCE.getMoneyAsInt();

			TransactionDB.INSTANCE.transferMoney(money);
			int balance = TransactionDB.INSTANCE.getBalance();
			String balanceAsString = NumberFormat.getNumberInstance().format(balance);
			TransactionDB.INSTANCE.addTx(new Transaction(name, 0, money, balance, new DateTime()));

			if (step == Step.TransferToSomeone) {
				ReceiveMessage receive = new ReceiveMessage(context.getString(R.string.dialog_chat_after_transfer, name, moneyAsString, balanceAsString));
				ConfirmRequest request = new ConfirmRequest();
				request.addInfoEvent(context.getResources().getString(R.string.dialog_button_recent_transaction), () -> {
					MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_button_recent_transaction)));
				});
				request.addPrimaryEvent(context.getResources().getString(R.string.dialog_button_transfer_add), () -> {
					MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_button_transfer_add)));
				});
				MessageBox.INSTANCE.addAndWait(receive, request);
				step = Step.TransferDone;
			} else if (step == Step.TransferByAI) {
				dbHelper.get(User.class)
						.subscribe(result -> {
							User user = result.last();

							TransactionDB.INSTANCE.setTxName(context.getString(R.string.dialog_chat_after_transfer_to_mother));
							TransactionDB.INSTANCE.setTxMoney("1,000,000");

							MessageBox.INSTANCE.add(new ReceiveMessage(context.getString(R.string.dialog_chat_after_transfer_by_ai, moneyAsString, balanceAsString, user.getName())));
							ConfirmRequest request = ConfirmRequest.buildYesOrNo(context);
							MessageBox.INSTANCE.add(request);

							step = Step.TransferToSomeone;
						});
			}
		}

		if (msg instanceof TransferButtonPressed) {
			MessageBox.INSTANCE.add(new RequestRemoveControls());

			String name = TransactionDB.INSTANCE.getTxName();
			String moneyAsString = TransactionDB.INSTANCE.getTxMoney();
			int money = TransactionDB.INSTANCE.getMoneyAsInt();
			MessageBox.INSTANCE.add(new SendMessage(context.getString(R.string.dialog_chat_send_transfer, name, moneyAsString)));

			if (money >= 1000000) {
				MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_finger_tip_sign)),
					new RequestSignature()
				);
			} else {
				MessageBox.INSTANCE.add(new MoneyTransferred());
			}
		}

		if (msg instanceof Done) {
			new RecommendScenarioMenuRequest(context);
			this.clear();
		}
	}

	@Override
	public void onUserSend(String msg) {
		switch (step) {
			case Initial:
				MessageBox.INSTANCE.add(new TransferTo(context.getResources().getString(R.string.dialog_chat_electricity_fare), 33750, TransferTo.TransactionType.ByAI));
				break;
			case TransferToSomeone:
			case TransferByAI:
				if (msg.equals(context.getString(R.string.dialog_button_yes))) {
					int money = TransactionDB.INSTANCE.getMoneyAsInt();

					if (money >= 1000000) {
						MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_finger_tip_sign)));
						MessageBox.INSTANCE.add(new RequestSignature());
					} else {
						MessageBox.INSTANCE.add(new MoneyTransferred());
					}
				} else if (msg.equals(context.getResources().getString(R.string.dialog_button_no))) {
					MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_transfer_cancel)));
					MessageBox.INSTANCE.add(new Done());
				} else if (msg.equals(context.getResources().getString(R.string.dialog_button_transfer_other))) {
					selectAccounts();
					step = Step.TransferToSomeone;
				}
				break;
			case TransferDone:
				if (msg.equals(context.getResources().getString(R.string.dialog_button_recent_transaction))) {
					dbHelper.get(User.class)
							.subscribe(users -> {
								MessageBox.INSTANCE.add(new ReceiveMessage(context.getString(R.string.dialog_chat_someone_recent_transaction, users.last().getName())));
								RecentTransaction rt = new RecentTransaction(TransactionDB.INSTANCE.getTx());
								MessageBox.INSTANCE.add(rt);
								MessageBox.INSTANCE.add(new Done());
							}, throwable -> {

							});
				} else if (msg.equals(context.getResources().getString(R.string.dialog_button_transfer_add))) {
					selectAccounts();
					step = Step.TransferToSomeone;
				} else {
					MessageBox.INSTANCE.add(new Done());
				}
				break;
		}
	}

	@Override
	public void clear() {
		isProceeding = true;
		step = Step.Initial;
		TransactionDB.INSTANCE.setTxMoney("");
		TransactionDB.INSTANCE.setTxName("");
	}

	@Override
	public boolean isProceeding() {
		return isProceeding;
	}

	private void selectAccounts() {
		List<Account> accounts = new ArrayList<>();
		accounts.add(new Account("어머니", "2017/01/25", "200,000 원 " + context.getString(R.string.string_transfer).toLowerCase(), true));
		accounts.add(new Account("박예린", "2017/01/11", "100,000 원 " + context.getString(R.string.string_transfer).toLowerCase(), false));
		accounts.add(new Account("김가람", "2017/01/11", "36,200 원 " + context.getString(R.string.string_transfer).toLowerCase(), false));
		accounts.add(new Account("김이솔", "2017/01/10", "100,000 원 " + context.getString(R.string.dialog_string_deposit).toLowerCase(), false));

		MessageBox.INSTANCE.add(new ReceiveMessage(context.getString(R.string.dialog_string_select_receiver)));
		MessageBox.INSTANCE.add(new AccountList(accounts));

		ConfirmRequest confirmRequest = new ConfirmRequest();
		confirmRequest.addInfoEvent(context.getString(R.string.dialog_contact), () -> {
			MessageBox.INSTANCE.add(new RequestSelectContact());
		}, false);
		MessageBox.INSTANCE.add(confirmRequest);
		MessageBox.INSTANCE.add(new RequestTransferUI());
	}

	private enum Step {
		Initial, TransferToSomeone, TransferByAI, TransferDone
	}
}
