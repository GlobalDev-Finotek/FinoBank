package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;

/**
 * Created by jungwon on 5/26/2017.
 */

public class CarLoan implements Scenario {
    private Context context;
    private Step step = Step.Initial;

    public CarLoan (Context context) {
        this.context = context;
    }

    @Override
    public String getName() {
        return context.getString(R.string.main_string_v2_login_loan_car) ;
    }


    @Override
    public boolean decideOn(String msg) {
        return false;
    }

    @Override
    public void onReceive(Object msg) {

    }

    @Override
    public void onUserSend(String msg) {
        switch (step) {
            case Initial:
                MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_car_ask)));
                MessageBox.INSTANCE.add(context.getResources().getString(R.string.main_string_v2_login_car_recommend));
                step = Step.question;
                break;
            case question:
                if(msg.equals(context.getResources().getString(R.string.main_string_v2_login_car_yes)))
                {
                    step= Step.loan;
                }
                else if (msg.equals(context.getResources().getString(R.string.main_string_v2_login_car_no)))
                {
                    MessageBox.INSTANCE.add(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_car_no_confirm)));
                }
                break;
            case loan:
                //계좌이체 프로세스 추가 필요

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

    private enum Step{
        Initial, question, loan
    }
}
