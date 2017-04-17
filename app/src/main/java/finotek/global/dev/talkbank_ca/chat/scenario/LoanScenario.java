package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.Agreement;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementRequest;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementResult;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.Transaction;
import finotek.global.dev.talkbank_ca.chat.messages.action.DismissKeyboard;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.RequestKeyboardInput;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;

public class LoanScenario implements Scenario {
    private Context context;
    private Step step = Step.Initial;

    public LoanScenario(Context context) {
        this.context = context;
    }

    @Override
    public String getName() {
        return context.getString(R.string.scenario_loan);
    }

    @Override
    public boolean decideOn(String msg) {
        switch(msg) {
            case "집을 담보로 대출 받고 싶어":
            case "소액 담보 대출":
            case "소액 담보대출":
            case "소액담보 대출":
            case "소액담보대출":
            case "담보 대출":
            case "담보대출":
            case "대출":
                return true;
            default:
                return msg.equals(context.getResources().getString(R.string.main_string_secured_mirocredit));
        }
    }

    // SendMessage를 제외한 모든 메시지를 받음
    @Override
    public void onReceive(Object msg) {
        if(msg instanceof SignatureVerified) {
            if(step == Step.Last) {
                // 입금내역 추가
                TransactionDB.INSTANCE.deposit(50000000);
                TransactionDB.INSTANCE.addTx(
                    new Transaction(context.getString(R.string.main_string_secured_mirocredit),
                        1, 50000000, TransactionDB.INSTANCE.getBalance(), new DateTime()
                    ));

                MessageBox.INSTANCE.add(new RequestRemoveControls());
                MessageBox.INSTANCE.add(new AgreementResult());

                MessageBox.INSTANCE.delay(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_loan_success)), 600);
                MessageBox.INSTANCE.delay(new Done(), 800);
            }
        }

        if(msg instanceof Done) {
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
                    MessageBox.INSTANCE.add(new RequestKeyboardInput());
                    step = Step.InputMoney;
                } else if(msg.equals(context.getResources().getString(R.string.dialog_button_no))){
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_microedit_cancel)));
                    MessageBox.INSTANCE.add(new Done());
                } else {
	                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_recognize_error)));
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
                required.addChild(new Agreement(101, context.getResources().getString(R.string.dialog_string_loan_service_user_agreement), "view.pdf"));
                required.addChild(new Agreement(102, context.getResources().getString(R.string.dialog_string_personal_credit_information_access_agreement), "view2.pdf"));
                required.addChild(new Agreement(103, context.getResources().getString(R.string.dialog_string_loan_transaction_agreement), "view3.pdf"));
                required.addChild(new Agreement(104, context.getResources().getString(R.string.dialog_string_contact_information), "view4.pdf"));

                Agreement optional = new Agreement(200, context.getResources().getString(R.string.dialog_string_optional_term_agreement));
                optional.addChild(new Agreement(201, context.getResources().getString(R.string.dialog_string_customer_information_agreement), "view5.pdf"));

                agreements.add(required);
                agreements.add(optional);

                MessageBox.INSTANCE.add(new AgreementRequest(agreements));
                MessageBox.INSTANCE.add(new DismissKeyboard());
                step = Step.Last;
                break;
            default:
	            MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_recognize_error)));
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
        Initial, InputAddress, InputMoney, Agreement, Last
    }
}
