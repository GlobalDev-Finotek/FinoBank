package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestTakeIDCard;
import finotek.global.dev.talkbank_ca.model.User;
import io.realm.Realm;

public class AccountScenario_v2 implements Scenario {
    private Context context;
    private Step step = Step.Initial;
    private User user;

    public AccountScenario_v2(Context context) {
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
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_open_account_complete)));
                MessageBox.INSTANCE.add(new Done());
            }
        }

        if (msg instanceof Done) {
            step = Step.Initial;
        }
    }

    public RecoMenuRequest getRequestConfirm() {
        RecoMenuRequest req = new RecoMenuRequest();
        //req.setTitle("추천메뉴");
        req.setDescription(context.getResources().getString(R.string.main_string_v2_login_electricity_open_account));

        req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.string_yes_ready), null);
        return req;
    }

    public RecoMenuRequest getIdPicConfirm() {
        RecoMenuRequest req = new RecoMenuRequest();
        //req.setTitle("추천메뉴");
        req.setDescription(context.getResources().getString(R.string.dialog_chat_correct_information));

        req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.string_yes_its_ok), null);
        req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.string_no_retake), null);
        return req;
    }

    @Override
    public void onUserSend(String msg) {
        switch (step) {
            case Initial:
                MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_open_account_notice)),
                getRequestConfirm()
                );
                //MessageBox.INSTANCE.add(ConfirmRequest.buildYesOrNo(context)); // 네, 아니오
                step = Step.AskOpen;
                break;

            case AskOpen:
                if(msg.equals(context.getString(R.string.string_yes_ready))) {
                    MessageBox.INSTANCE.add(new RequestTakeIDCard());
                    getIdPicConfirm();
                    step = Step.Retake;
                }
                break;

            case Retake:
                if(msg.equals(context.getString(R.string.string_yes_its_ok))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_additional_picture)));
                    MessageBox.INSTANCE.add(ConfirmRequest.buildYesOrNo(context)); // 네, 아니오
                    step = Step.OtherIDs;
                }
                else {
                    getIdPicConfirm();
                    step = Step.Retake;
                }

/*




                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_additional_picture)));
                    MessageBox.INSTANCE.add(ConfirmRequest.buildYesOrNo(context)); // 네, 아니오
                    step = Step.CheckIDCard;
                }
                else if (msg.equals(context.getString(R.string.string_no))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_cancel_opening_bank)));
                    MessageBox.INSTANCE.add(new Done());
                } else {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_recognize_error)));
                }*/
                break;

            case CheckIDCard:
                if(msg.equals(context.getString(R.string.string_yes))) {
                    MessageBox.INSTANCE.add(new RequestTakeIDCard());
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_additional_picture)));
                    MessageBox.INSTANCE.add(ConfirmRequest.buildYesOrNo(context)); // 네, 아니오
                    break;
                }
                else if(msg.equals(context.getString(R.string.string_no))){
                    step = Step.TakeSign;
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_sign_necessary)));
                    MessageBox.INSTANCE.add(ConfirmRequest.buildYesOrNo(context)); // 네, 아니오
                    break;
                }


            // 본인이 맞으세요?
            case TakeSign:
                if(msg.equals(context.getString(R.string.string_yes))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_open_account_sign_tip)));
                    MessageBox.INSTANCE.add(new RequestSignature());
                    step = Step.Last;
                } else if (msg.equals(context.getString(R.string.string_no))) {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_id_card_retake)));
                    MessageBox.INSTANCE.add(new RequestTakeIDCard());
                } else {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_recognize_error)));
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
        Initial, CheckIDCard, TakeSign, Last, AskOpen, Retake, OtherIDs
    }
}
