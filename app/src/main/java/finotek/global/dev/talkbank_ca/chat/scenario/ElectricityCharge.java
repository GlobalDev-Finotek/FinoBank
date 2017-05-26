package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import java.text.NumberFormat;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.Transaction;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;

/**
 * Created by jungwon on 5/26/2017.
 */

public class ElectricityCharge implements Scenario {
    private Context context;
    private Step step = Step.Initial;

    public ElectricityCharge(Context context) {
        this.context = context;
    }

    @Override
    public String getName() {
        return context.getString(R.string.main_string_v2_login_pay_electricity) ;
    }


    @Override
    public boolean decideOn(String msg) {
        return msg.equals(R.string.main_string_v2_login_pay_electricity) ||
                msg.equals("전기") || msg.equals("전기료 이체") || msg.equals("공과금") || msg.equals("Pay electricity");
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof Done) {
            MessageBox.INSTANCE.addAndWait(
                    new SendMessage(context.getResources().getString(R.string.main_string_open_account)));
            step = Step.Initial;
        }

    }

    public RecoMenuRequest getRequestConfirm() {
        RecoMenuRequest req = new RecoMenuRequest();
        //req.setTitle("추천메뉴");
        req.setDescription(context.getResources().getString(R.string.main_string_v2_login_electricity_recommend));

        req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_v2_login_electricity_yes), null);
        req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.main_string_v2_login_electricity_no), null);
        return req;
    }

    @Override
    public void onUserSend(String msg) {
        LeftScenario.scenarioList.remove("E");
        switch (step) {
            case Initial:
                MessageBox.INSTANCE.addAndWait(
                        new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_message)),
                        new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_compare,
                                NumberFormat.getInstance().format(51490), NumberFormat.getInstance().format(35460))),
                        getRequestConfirm()
                );
                step = Step.question;
                break;
            case question:
                if(msg.equals(context.getResources().getString(R.string.main_string_v2_login_electricity_yes)))
                {
                    MessageBox.INSTANCE.add(
                            new SendMessage(context.getResources().getString(R.string.main_string_open_account)));

                    //step=Step.openTransfer;
                }
                else if (msg.equals(context.getResources().getString(R.string.main_string_v2_login_electricity_no)))
                {

                    MessageBox.INSTANCE.addAndWait(
                            new ReceiveMessage(context.getResources().getString(
                                    R.string.main_string_v2_login_electricity_account_confirm,
                                    NumberFormat.getInstance().format(51490))));
                    TransactionDB.INSTANCE.deposit(-51490);
                }
                break;
            /*case openTransfer:
                //계좌이체 프로세스 추가 필요

                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_open_account)));

                //촬영 프로세스 추가 필요

                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_additional_picture)));

                if(msg.equals(context.getResources().getString(R.string.main_string_v2_login_electricity_additional_picture_yes)))
                {
                    step=Step.againPicture;
                }
                else if (msg.equals(context.getResources().getString(R.string.main_string_v2_login_electricity_additional_picture_no)))
                {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_signature)));

                    //서명 프로세스 추가 필요

                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_account_confirm)));

                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_ask_step)));
                    MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_open_saving_account));
                    MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_house_loan));
                    MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_notify_again));
                }
                break;*/
            /*case onlyTransfer:
                //계좌이체 프로세스 추가 필요
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_confirm)));
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_balance)));

                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_ask_step)));
                MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_open_saving_account));
                MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_house_loan));
                MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_notify_again));

                break;*/
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isProceeding() {
        return false;
    }

    private enum Step {
        Initial, question, onlyTransfer, openTransfer, againPicture
    }
}
