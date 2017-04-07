package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;

public class SendMailScenario implements Scenario {
    int step = 0;
    private Context context;

    public SendMailScenario(Context context) {
        this.context = context;
    }

    @Override
    public boolean decideOn(String msg) {
        return msg.equals(context.getResources().getString(R.string.main_button_send_the_conversation_to_e_mail));
    }

    @Override
    public void onReceive(Object msg) {
        if(msg instanceof Done) {
            step = 0;
        }
    }

    @Override
    public void onUserSend(String msg) {
        if(step == 0) {
            MessageBox.INSTANCE.add(new ReceiveMessage("상담 내용을 이메일로 보내시겠습니까?"));
            MessageBox.INSTANCE.add(ConfirmRequest.buildYesOrNo(context));
            step = 1;
        } else if(step == 1) {
            if (msg.equals(context.getString(R.string.dialog_button_yes))) {
                MessageBox.INSTANCE.add(new ReceiveMessage("발송되었습니다"));
                MessageBox.INSTANCE.add(new Done());
            } else if (msg.equals(context.getString(R.string.dialog_button_no))) {
                MessageBox.INSTANCE.add(new ReceiveMessage("취소되었습니다."));
                MessageBox.INSTANCE.add(new Done());
            }
        }
    }

    @Override
    public void clear() {
        step = 0;
    }
}
