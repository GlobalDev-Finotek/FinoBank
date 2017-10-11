package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SucceededMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.control.DonateItem;
import finotek.global.dev.talkbank_ca.chat.messages.control.DonateRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.model.User;
import io.realm.Realm;

public class DonateScenario implements Scenario {
	private Step step = Step.initial;
	private Context context;

	private int selected = -1;

	public DonateScenario(Context context) {
		this.context = context;
	}

	@Override
	public boolean decideOn(String msg) {
		return msg.equals(context.getResources().getString(R.string.dialog_chat_donate_menu));
	}

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof TransferButtonPressed) {
			String money = TransactionDB.INSTANCE.getTxMoney();
			String targetName = getTargetName();
			String userName = getUserName();
			Log.d("FINOTEK", "You donated " + Integer.valueOf(money.replace(",", "")) + " won");
			int coin = (int) (((Integer.valueOf(money.replace(",", ""))) / 1000) * 1.3);

			MessageBox.INSTANCE.addAndWait(
					new SucceededMessage(context.getResources().getString(R.string.contextlog_authentication_succeeded, userName, 85.2)),
					new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_donate_result, coin, targetName)),
					new Done()
			);
		}

		if (msg instanceof Done) {
			MessageBox.INSTANCE.add(new RecommendScenarioMenuRequest(context));
		}
	}

	@Override
	public void onUserSend(String msg) {
		switch (step) {
			case initial:
				// 기부할 사람 고르는 메뉴 보여주기
				List<DonateItem> menus = new ArrayList<>();
				menus.add(new DonateItem(R.drawable.donate1, context.getResources().getString(R.string.dialog_chat_donate_menu1), () -> {
					MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_chat_donate_target1)));
					selected = 1;
				}));
				menus.add(new DonateItem(R.drawable.donate2, context.getResources().getString(R.string.dialog_chat_donate_menu2), () -> {
					MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_chat_donate_target2)));
					selected = 2;
				}));
				menus.add(new DonateItem(R.drawable.donate3, context.getResources().getString(R.string.dialog_chat_donate_menu3), () -> {
					MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.dialog_chat_donate_target3)));
					selected = 3;
				}));
				String title = context.getResources().getString(R.string.dialog_chat_donate_title);
				String description = context.getResources().getString(R.string.dialog_chat_donate_description);
				MessageBox.INSTANCE.add(new DonateRequest(title, description, menus));
				step = Step.preselect;
				break;
			case preselect:
				String userName = getUserName();
				String target = getTargetName();

				TransactionDB.INSTANCE.setTxName(target);

				RequestTransferUI req = new RequestTransferUI();
				req.setEnabled(true);

				MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_donate_preprocess, userName)), req);
				break;
		}
	}

	@Override
	public String getName() {
		return context.getResources().getString(R.string.dialog_chat_donate);
	}

	@Override
	public void clear() {
		step = Step.initial;
		selected = -1;
	}

	@Override
	public boolean isProceeding() {
		return false;
	}

	private String getTargetName() {
		if (selected == 1)
			return context.getResources().getString(R.string.dialog_chat_donate_target_name1);

		if (selected == 2)
			return context.getResources().getString(R.string.dialog_chat_donate_target_name2);

		if (selected == 3)
			return context.getResources().getString(R.string.dialog_chat_donate_target_name3);

		return "none";
	}

	private String getUserName() {
		Realm realm = Realm.getDefaultInstance();
		User user = realm.where(User.class).findAll().last();
		String userName = "홍길동";

		if (user != null)
			userName = user.getName();

		return userName;
	}

	private enum Step {
		initial, preselect
	}
}

