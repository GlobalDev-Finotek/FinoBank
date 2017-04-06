package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.Agreement;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementRequest;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementResult;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.DismissKeyboard;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;

public class LoanScenario implements Scenario {
    private Context context;
    private Step step = Step.Initial;

    public LoanScenario(Context context) {
        this.context = context;
    }

    @Override
    public boolean decideOn(String msg) {
        switch(msg) {
            case "집을 담보로 대출 받고 싶어":
            case "소액 담보 대출":
                return true;
            default:
                return msg.equals(context.getResources().getString(R.string.main_string_secured_mirocredit));
        }
    }

    // SendMessage를 제외한 모든 메시지를 받음
    @Override
    public void onReceive(Object msg) {
        if(msg instanceof Done) {
            if(step == Step.Last) {
                MessageBox.INSTANCE.add(new AgreementResult());
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_loan_success)));
            }

            step = Step.Initial;
        }
    }

    // 사용자가 톡뱅에게 메시지를 보낼 때 호출되는 이벤트
    @Override
    public void onUserSend(String msg) {
        switch(step){
            case Initial:
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_loan_apply)));
                MessageBox.INSTANCE.add(ConfirmRequest.buildYesOrNo(context));
                step = Step.InputAddress;
                break;
            case InputAddress:
                if(msg.equals(context.getResources().getString(R.string.dialog_button_yes))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_string_home_address_type)));
                    step = Step.InputMoney;
                } else if(msg.equals(context.getResources().getString(R.string.dialog_button_no))){
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_microedit_cancel)));
                    MessageBox.INSTANCE.add(new Done());
                } else {
                    MessageBox.INSTANCE.add(new ReceiveMessage("무슨 의미 인가요?"));
                }
                break;
            case InputMoney:
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_loan_amount_type)));
                step = Step.Agreement;
                break;
            case Agreement:
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_string_term_agreement_accept)));

                List<Agreement> agreements = new ArrayList<>();
                Agreement required = new Agreement(100, context.getResources().getString(R.string.dialog_string_mandatory_term_accept));
                required.addChild(new Agreement(101, context.getResources().getString(R.string.dialog_string_loan_service_user_agreement)));
                required.addChild(new Agreement(102, context.getResources().getString(R.string.dialog_string_personal_credit_information_access_agreement)));
                required.addChild(new Agreement(103, context.getResources().getString(R.string.dialog_string_loan_transaction_agreement)));
                required.addChild(new Agreement(104, context.getResources().getString(R.string.dialog_string_contact_information)));

                Agreement optional = new Agreement(200, context.getResources().getString(R.string.dialog_string_optional_term_agreement));
                optional.addChild(new Agreement(201, context.getResources().getString(R.string.dialog_string_customer_information_agreement)));

                agreements.add(required);
                agreements.add(optional);

                MessageBox.INSTANCE.add(new AgreementRequest(agreements));
                MessageBox.INSTANCE.add(new DismissKeyboard());
                step = Step.Last;
                break;
            default:
                MessageBox.INSTANCE.add(new ReceiveMessage("무슨 의미 인가요?"));
                break;
        }
    }

    private enum Step {
        Initial, InputAddress, InputMoney, Agreement, Last
    }
}
