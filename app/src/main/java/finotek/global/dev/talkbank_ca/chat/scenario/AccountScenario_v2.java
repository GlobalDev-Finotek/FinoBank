package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RemoteCallCompleted;
import finotek.global.dev.talkbank_ca.chat.messages.RequestRemoteCall;
import finotek.global.dev.talkbank_ca.chat.messages.RequestTakeAnotherIDCard;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;

import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestPhoto;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;

public class AccountScenario_v2 implements Scenario {
	private Context context;
	private Step step = Step.Initial;
    private String yes = "";
    private String no = "";

	public AccountScenario_v2(Context context) {
		this.context = context;
        yes = context.getString(R.string.string_yes);
        no = context.getString(R.string.string_no);
	}

	@Override
	public String getName() {
		return context.getString(R.string.scenario_account);
	}

	@Override
	public boolean decideOn(String msg) {
		return msg.equals(context.getResources().getString(R.string.main_string_open_account)) || msg.equals("계좌개설") ||
				msg.equals(context.getResources().getString(R.string.main_string_recommend_travel_yes));
	}

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof SignatureVerified) {
			if (step == Step.Last) {
				MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_open_account_complete)),
						new Done(),
						new RecommendScenarioMenuRequest(context)
				);
			}
		}

		// 화상 연결이 종료될 때 서명 인증을 다시 요청한다.
		if(msg instanceof RemoteCallCompleted) {
            step = Step.TakeSign;
            MessageBox.INSTANCE.addAndWait(
                    RecoMenuRequest.buildYesOrNo(context, context.getResources().getString(R.string.main_string_v2_sign_necessary) + " " +
                            context.getResources().getString(R.string.dialog_chat_open_account_sign_tip))
            );
        }

		if (msg instanceof Done) {
			new RecommendScenarioMenuRequest(context);
			step = Step.Initial;
		}
	}

	@Override
	public void onUserSend(String msg) {
		switch (step) {
			case Initial:
				TransactionDB.INSTANCE.setTxName("");
				TransactionDB.INSTANCE.setTxMoney("");

				MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_open_account_notice)),
						new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_open_account)),
						RecoMenuRequest.buildYesOrNo(context, context.getResources().getString(R.string.main_string_v2_login_electricity_open_account_2))
				);

				step = Step.AskOpen;
				break;

			case AskOpen:
				if (msg.equals(yes)) {
					MessageBox.INSTANCE.addAndWait(new RequestPhoto());
					step = Step.CheckIDCard;
				} else if (msg.equals(no)) {
					MessageBox.INSTANCE.addAndWait(new Done(),
							new ReceiveMessage(context.getResources().getString(R.string.dialog_string_email_cancel)),
							new RecommendScenarioMenuRequest(context));
					step = Step.CheckIDCard;
				}

				break;

			case CheckIDCard:
				if (msg.equals(yes)) {
					MessageBox.INSTANCE.addAndWait(new RequestTakeAnotherIDCard());
					break;
				} else if (msg.equals(no)) {
					step = Step.RemoteCall;
                    MessageBox.INSTANCE.addAndWait(RecoMenuRequest.buildYesOrNo(context, context.getResources().getString(R.string.dialog_chat_remote_call_request)));
					break;
				}

				// 화상 연결 요청
			case RemoteCall:
                if (msg.equals(yes)) {
                    MessageBox.INSTANCE.add(new RequestRemoteCall());
                } else if (msg.equals(no)) {
                    MessageBox.INSTANCE.addAndWait(new ReceiveMessage(""), new Done());
                }
				break;

				// 본인이 맞으세요?
			case TakeSign:
				if (msg.equals(yes)) {
					MessageBox.INSTANCE.addAndWait(
							new RequestSignature()
					);
					step = Step.Last;
				} else if (msg.equals(no)) {
					MessageBox.INSTANCE.addAndWait(
							new Done(), new RecommendScenarioMenuRequest(context)
					);
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
		Initial, CheckIDCard, RemoteCall, TakeSign, Last, AskOpen, Retake, OtherIDs
	}
}