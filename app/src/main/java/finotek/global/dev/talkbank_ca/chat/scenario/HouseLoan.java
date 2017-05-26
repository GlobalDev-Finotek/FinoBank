package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.model.User;
import io.realm.Realm;

/**
 * Created by jungwon on 5/26/2017.
 */

public class HouseLoan implements Scenario {
    private Context context;
    private Step step = Step.Initial;
    private User user;

    public HouseLoan(Context context) {
        Realm realm = Realm.getDefaultInstance();
        this.user = realm.where(User.class).findAll().last();
        this.context = context;
    }

    @Override
    public String getName() {
        return context.getString(R.string.main_string_v2_login_house_loan) ;
    }


    @Override
    public boolean decideOn(String msg) {
         return msg.equals(context.getResources().getString(R.string.main_string_v2_login_house_loan));

    }

    @Override
    public void onReceive(Object msg) {
        if(msg instanceof Done){
            MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_secured_mirocredit)));
        }
    }

    @Override
    public void onUserSend(String msg) {
        LeftScenario.scenarioList.remove("H");
        switch (step) {
            case Initial:
                step = Step.question;
                MessageBox.INSTANCE.addAndWait(
                    new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_house_ask)),
                    new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_house_recommend)),
                    ConfirmRequest.buildYesOrNo(context)
                );
                break;
            case question:
                if(msg.equals(context.getResources().getString(R.string.string_yes))) {
                    MessageBox.INSTANCE.add(new Done());
                } else if (msg.equals(context.getResources().getString(R.string.string_no))) {
                    MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_house_no_confirm, user.getName())));
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

    private enum Step{
        Initial, question, loan
    }
}
