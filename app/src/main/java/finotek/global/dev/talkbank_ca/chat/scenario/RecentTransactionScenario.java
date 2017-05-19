package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RecentTransaction;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.model.DBHelper;
import finotek.global.dev.talkbank_ca.model.User;

public class RecentTransactionScenario implements Scenario {
	private Context context;
	private DBHelper dbHelper;

	public RecentTransactionScenario(Context context, DBHelper dbHelper) {
		this.context = context;
		this.dbHelper = dbHelper;
	}

	@Override
	public boolean decideOn(String msg) {
		return msg.equals("계좌조회") || msg.equals("계좌 조회") || msg.equals("최근거래내역") ||
				msg.equals("최근 거래 내역") || msg.equals(context.getString(R.string.dialog_button_recent_transaction)) ||
				msg.equals(context.getString(R.string.main_string_view_account_details)) || msg.equals("잔액조회") || msg.equals("잔액 조회") || msg.equals("거래내역")
				|| msg.equals("잔액") || msg.equals("최근거래 보기") || msg.equals("최근 거래내역 보기");
	}

	@Override
	public void onReceive(Object msg) {

	}

	@Override
	public void onUserSend(String msg) {
		dbHelper.get(User.class).subscribe(users -> {
			MessageBox.INSTANCE.add(new ReceiveMessage(context.getString(R.string.dialog_chat_someone_recent_transaction,
					users.last().getName())));
			RecentTransaction rt = new RecentTransaction(TransactionDB.INSTANCE.getTx());
			MessageBox.INSTANCE.add(rt);
			MessageBox.INSTANCE.add(new Done());
		}, throwable -> {

		});
	}

	@Override
	public String getName() {
		return context.getString(R.string.dialog_button_recent_transaction);
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean isProceeding() {
		return true;
	}
}
