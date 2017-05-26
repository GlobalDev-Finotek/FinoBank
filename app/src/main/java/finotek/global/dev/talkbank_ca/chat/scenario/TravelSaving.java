package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.model.User;
import io.realm.Realm;

/**
 * Created by jungwon on 5/26/2017.
 */

public class TravelSaving implements Scenario {
    private Context context;
    private Step step = Step.Initial;
    private User user;

    public TravelSaving (Context context) {
        Realm realm = Realm.getDefaultInstance();
        this.user = realm.where(User.class).findAll().last();
        this.context = context;
    }

    @Override
    public String getName() {
        return context.getString(R.string.main_string_v2_login_open_saving_account) ;
    }

    @Override
    public boolean decideOn(String msg) {
        return msg.equals("여행") || msg.equals("여행 적금") || msg.equals("적금") || msg.equals(context.getResources().getString(R.string.main_string_v2_login_open_saving_account)) || msg.equals("Travel savings");
    }

    @Override
    public void onReceive(Object msg) {

    }


    @Override
    public void onUserSend(String msg) {
        LeftScenario.scenarioList.remove("T");
        switch (step) {
            case Initial:
                step = Step.question;

                RecoMenuRequest req = new RecoMenuRequest();

                req.setDescription(context.getResources().getString(R.string.main_string_v2_open_account_notice));
                req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.string_yes), null);
                req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.string_no), null);


                MessageBox.INSTANCE.addAndWait(
                        new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_saving_ask)),
                        new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_saving_recommend)),
                        req

                );

                break;
            case question:
                if(msg.equals(context.getResources().getString(R.string.string_yes)))
                {
                    MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_open_account)));

                }
                else if (msg.equals(context.getResources().getString(R.string.string_no))) {
                    MessageBox.INSTANCE.addAndWait(
                      new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_saving_no_confirm, user.getName())));
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
    Initial, question
    }
}
