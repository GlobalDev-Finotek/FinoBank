package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestTakeIDCard;

public class AccountScenario implements Scenario {
    private Context context = MyApplication.getContext();

    private enum Step {
        Initial, CheckIDCard, TakeSign, Last
    }
    private Step step = Step.Initial;

    @Override
    public boolean decideOn(String msg) {
        return msg.equals("계좌개설") || msg.equals(context.getResources().getString(R.string.main_string_open_account));


    }

    @Override
    public void onReceive(Object msg) {
        if(msg instanceof Done) {
            if(step == Step.Last) {
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_open_account_success)));
            }

            step = Step.Initial;
        }
    }

    @Override
    public void onUserSend(String msg) {
        switch (step) {
            case Initial:
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_recommandation)));
                MessageBox.INSTANCE.add(ConfirmRequest.buildYesOrNo()); // 네, 아니오
                step = Step.CheckIDCard;
                break;
            case CheckIDCard:
                if(msg.equals(context.getResources().getString(R.string.dialog_button_yes))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_take_picture_id_card)));
                    MessageBox.INSTANCE.add(new RequestTakeIDCard());
                    step = Step.TakeSign;
                } else if(msg.equals(context.getResources().getString(R.string.dialog_button_no))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_cancel_opening_bank)));
                    MessageBox.INSTANCE.add(new Done());
                } else {
                    MessageBox.INSTANCE.add(new ReceiveMessage("무슨 말씀인지 잘 모르겠습니다."));
                }
                break;
            // 본인이 맞으세요?
            case TakeSign:
                if(msg.equals("네")) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_finger_tip_sign)));
                    MessageBox.INSTANCE.add(new RequestSignature());
                    step = Step.Last;
                } else if(msg.equals("아니오")){
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_cancel_opening_bank)));
                    MessageBox.INSTANCE.add(new Done());
                } else {
                    MessageBox.INSTANCE.add(new ReceiveMessage("무슨 말씀인지 잘 모르겠습니다."));
                }
                break;
        }
    }
}
