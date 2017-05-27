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
        return msg.equals(R.string.main_string_recommend_electric_title) ||
                msg.equals("Payment of electricity bill") || msg.equals("전기료 납부") || msg.equals("Pay electricity");
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
        req.setDescription(context.getResources().getString(R.string.main_string_recommend_electric_transfer_ask));


        req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_recommend_electric_transfer_yes), null);
        req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.main_string_recommend_electric_transfer_no), null);
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
                if(msg.equals(context.getResources().getString(R.string.main_string_recommend_electric_transfer_yes)))
                {
                    String message;
                    if(TransactionDB.INSTANCE.getBalance() < 51490)
                        message = context.getResources().getString(
                                R.string.main_string_recommend_electric_transfer_fail);
                    else {
                        TransactionDB.INSTANCE.deposit(-51490);
                        message = context.getResources().getString(
                                R.string.main_string_recommend_electric_transfer_success,
                                NumberFormat.getInstance().format(51490), NumberFormat.getInstance().format(TransactionDB.INSTANCE.getBalance()));
                    }

                    MessageBox.INSTANCE.addAndWait(
                            new ReceiveMessage(message),
                            //new Done(),
                            new RecommendScenarioMenuRequest(context)

                    );
                }
                else if (msg.equals(context.getResources().getString(R.string.main_string_recommend_electric_transfer_no)))
                {

                    MessageBox.INSTANCE.addAndWait(
                            //new Done(),
                            new ReceiveMessage(context.getResources().getString(R.string.main_string_recommend_electric_transfer_cancle)),
                            new RecommendScenarioMenuRequest(context)
                    );
                }
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
