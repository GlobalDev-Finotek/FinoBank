package finotek.global.dev.brazil.chat.scenario;

import android.content.Context;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.MessageBox;
import finotek.global.dev.brazil.chat.messages.ReceiveMessage;
import finotek.global.dev.brazil.chat.messages.RecentTransaction;
import finotek.global.dev.brazil.chat.messages.action.Done;
import finotek.global.dev.brazil.chat.storage.TransactionDB;
import finotek.global.dev.brazil.model.User;
import io.realm.Realm;

public class RecentTransactionScenario implements Scenario {
	private Context context;

	public RecentTransactionScenario(Context context) {
		this.context = context;
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
		User user = Realm.getDefaultInstance().where(User.class).findFirst();
		MessageBox.INSTANCE.addAndWait(
				new ReceiveMessage(context.getString(R.string.dialog_chat_someone_recent_transaction,
						user.getName())),
				new RecentTransaction(TransactionDB.INSTANCE.getTx()),
				new Done()
		);

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
