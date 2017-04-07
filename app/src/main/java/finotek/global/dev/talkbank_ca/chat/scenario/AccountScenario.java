package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestTakeIDCard;

public class AccountScenario implements Scenario {
    private Context context;
    private Step step = Step.Initial;

    public AccountScenario(Context context) {
        this.context = context;
    }

    @Override
    public boolean decideOn(String msg) {
        return msg.equals("계좌개설") || msg.equals(context.getResources().getString(R.string.main_string_open_account));
    }

    @Override
    public void onReceive(Object msg) {
        if(msg instanceof SignatureVerified) {
            if(step == Step.Last) {
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_open_account_success)));
                MessageBox.INSTANCE.add(new Done());
            }
        }

        if(msg instanceof Done) {
            step = Step.Initial;
        }
    }

    @Override
    public void onUserSend(String msg) {
        switch (step) {
            case Initial:
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_recommandation)));
                MessageBox.INSTANCE.add(ConfirmRequest.buildYesOrNo(context)); // 네, 아니오
                step = Step.CheckIDCard;
                break;
            case CheckIDCard:
                if (msg.equals(context.getString(R.string.string_yes))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_take_picture_id_card)));
                    MessageBox.INSTANCE.add(new RequestTakeIDCard());
                    step = Step.TakeSign;
                } else if (msg.equals(context.getString(R.string.string_no))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_cancel_opening_bank)));
                    MessageBox.INSTANCE.add(new Done());
                } else {
                    MessageBox.INSTANCE.add(new ReceiveMessage("무슨 말씀인지 잘 모르겠습니다."));
                }
                break;
            // 본인이 맞으세요?
            case TakeSign:
                if (msg.equals(context.getString(R.string.string_yes))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_finger_tip_sign)));
                    MessageBox.INSTANCE.add(new RequestSignature());
                    step = Step.Last;
                } else if (msg.equals(context.getString(R.string.string_no))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_cancel_opening_bank)));
                    MessageBox.INSTANCE.add(new Done());
                } else {
                    MessageBox.INSTANCE.add(new ReceiveMessage("무슨 말씀인지 잘 모르겠습니다."));
                }
                break;
        }
    }

    @Override
    public void clear() {
        step = Step.Initial;
    }

    private enum Step {
        Initial, CheckIDCard, TakeSign, Last
    }
}
