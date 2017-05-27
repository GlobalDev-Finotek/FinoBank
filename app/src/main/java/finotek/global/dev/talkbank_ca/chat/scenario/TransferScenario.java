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
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
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

            RecommendScenarioMenuRequest request = new RecommendScenarioMenuRequest(context);
            request.setTitle("");
            request.setDescription(context.getString(R.string.dialog_chat_after_transfer, name, moneyAsString, balanceAsString));

            MessageBox.INSTANCE.addAndWait(request, new Done());
            step = Step.TransferDone;
        }

		if (msg instanceof Done) {
			this.clear();
		}
	}

	@Override
	public void onUserSend(String msg) {
		switch (step) {
			case Initial:
				selectAccounts();
				step = Step.TransferToSomeone;
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

		ConfirmRequest confirmRequest = new ConfirmRequest();
		confirmRequest.addInfoEvent(context.getString(R.string.dialog_contact), () -> {
			MessageBox.INSTANCE.add(new RequestSelectContact());
		}, false);

        MessageBox.INSTANCE.addAndWait(
            new ReceiveMessage(context.getString(R.string.dialog_string_select_receiver)),
            new AccountList(accounts),
            confirmRequest,
            new RequestTransferUI()
        );
	}

	private enum Step {
		Initial, TransferToSomeone, TransferByAI, TransferDone
	}
}

