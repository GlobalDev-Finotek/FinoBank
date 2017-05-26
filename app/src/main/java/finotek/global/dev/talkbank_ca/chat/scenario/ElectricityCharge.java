package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;

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
        return msg.equals("전기") || msg.equals("전기 요금") || msg.equals("공과금");
    }

    @Override
    public void onReceive(Object msg) {

    }

    @Override
    public void onUserSend(String msg) {
        switch (step) {
            case Initial:
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_message)));
                MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_electricity_compare));
                MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_electricity_recommend));
                step = Step.question;
                break;
            case question:
                if(msg.equals(context.getResources().getString(R.string.main_string_v2_login_electricity_yes)))
                {
                    step=Step.openTransfer;
                }
                else if (msg.equals(context.getResources().getString(R.string.main_string_v2_login_electricity_no)))
                {
                    step=Step.onlyTransfer;
                }
                break;
            case openTransfer:
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
                    MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_loan_house));
                    MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_notify_again));
                }
                break;
            case onlyTransfer:
                //계좌이체 프로세스 추가 필요
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_confirm)));
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_electricity_balance)));

                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_ask_step)));
                MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_open_saving_account));
                MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_loan_house));
                MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_notify_again));

                break;
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
