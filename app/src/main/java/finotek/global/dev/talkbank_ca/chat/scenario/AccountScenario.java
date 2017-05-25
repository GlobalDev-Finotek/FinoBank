package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestTakeIDCard;
import finotek.global.dev.talkbank_ca.model.User;
import io.realm.Realm;

public class AccountScenario implements Scenario {
	private Context context;
	private Step step = Step.Initial;
	private User user;

	public AccountScenario(Context context) {
		Realm realm = Realm.getDefaultInstance();
		this.user = realm.where(User.class).findAll().last();
		this.context = context;
	}

	@Override
	public String getName() {
		return context.getString(R.string.scenario_account);
	}

	@Override
	public boolean decideOn(String msg) {
		return msg.equals(context.getResources().getString(R.string.main_string_open_account)) || msg.equals("계좌개설");
	}

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof SignatureVerified) {
			if (step == Step.Last) {
				MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_open_account_success)),
					new Done()
				);
			}
		}

		if (msg instanceof Done) {
			step = Step.Initial;
		}
	}

	@Override
	public void onUserSend(String msg) {
		switch (step) {
			case Initial:
				MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_recommandation, user.getName())),
					ConfirmRequest.buildYesOrNo(context)
				);
				step = Step.CheckIDCard;
				break;
			case CheckIDCard:
				if (msg.equals(context.getString(R.string.string_yes))) {
					MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_take_picture_id_card)),
						new RequestTakeIDCard()
					);
					step = Step.TakeSign;
				} else if (msg.equals(context.getString(R.string.string_no))) {
					MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_cancel_opening_bank)),
						new Done()
					);
				} else {
					MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_recognize_error)));
				}
				break;
			// 본인이 맞으세요?
			case TakeSign:
				if (msg.equals(context.getString(R.string.string_yes))) {
					MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_open_account_sign_tip)),
						new RequestSignature()
					);
					step = Step.Last;
				} else if (msg.equals(context.getString(R.string.string_no))) {
					MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_id_card_retake)),
						new RequestTakeIDCard()
					);
				} else {
					MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_recognize_error)));
				}
				break;
		}
	}

	@Override
	public void clear() {
		step = Step.Initial;
	}

	@Override
	public boolean isProceeding() {
		return true;
	}

	private enum Step {
		Initial, CheckIDCard, TakeSign, Last
	}
}
