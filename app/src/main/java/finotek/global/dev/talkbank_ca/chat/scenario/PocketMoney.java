package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import java.text.NumberFormat;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;

/**
 * Created by KoDeokyoon on 2017. 5. 27..
 */

public class PocketMoney implements Scenario{

    private Context context;
    private Step step = Step.Initial;

    public PocketMoney(Context context) {
        this.context = context;
    }

    @Override
    public String getName() {
        return context.getString(R.string.main_string_recommend_parents_title) ;
    }


    @Override
    public boolean decideOn(String msg) {
        return msg.equals(R.string.main_string_recommend_parents_title) ||
                msg.equals("Parents pocket money") || msg.equals("부모님 용돈");
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
        req.setDescription(context.getResources().getString(R.string.main_string_recommend_parents_ask));

        req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_recommend_parents_yes), null);
        req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.main_string_recommend_parents_no), null);
        return req;
    }

    @Override
    public void onUserSend(String msg) {
        LeftScenario.scenarioList.remove("P");
        switch (step) {
            case Initial:
                MessageBox.INSTANCE.addAndWait(
                        new ReceiveMessage(context.getResources().getString(R.string.main_string_recommend_parents_intro)),
                        getRequestConfirm()
                );
                step = Step.question;
                break;
            case question:
                if(msg.equals(context.getResources().getString(R.string.main_string_recommend_parents_yes)))
                {
                    String message;
                    if(TransactionDB.INSTANCE.getMainBalance() < 300000)
                        message = context.getResources().getString(
                                R.string.main_string_recommend_parents_fail);
                    else {
                        TransactionDB.INSTANCE.deposit(-300000);
                        message = context.getResources().getString(
                                R.string.main_string_recommend_parents_success, NumberFormat.getInstance().format(TransactionDB.INSTANCE.getMainBalance()));
                    }

                    MessageBox.INSTANCE.addAndWait(
                            new ReceiveMessage(message),
                            //new Done(),
                            new RecommendScenarioMenuRequest(context)

                    );
                }
                else if (msg.equals(context.getResources().getString(R.string.main_string_recommend_parents_no)))
                {

                    MessageBox.INSTANCE.addAndWait(
                            //new Done(),
                            new ReceiveMessage(context.getResources().getString(R.string.main_string_recommend_parents_cancle)),
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
