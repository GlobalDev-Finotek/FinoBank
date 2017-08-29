package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;


public class ScoreCalculationScenario implements Scenario {
    private Context context;

    public ScoreCalculationScenario(Context context) {
        this.context = context;
    }

    @Override
    public boolean decideOn(String msg) {
        return msg.equals(context.getResources().getString(R.string.dialog_chat_how_score_is_computed));
    }

    @Override
    public void onReceive(Object msg) {

    }

    @Override
    public void onUserSend(String msg) {
        MessageBox.INSTANCE.addAndWait(
            new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_how_to_compute_score)),
            new Done()
        );
    }

    @Override
    public String getName() {
        return context.getResources().getString(R.string.dialog_chat_contextlog_search);
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isProceeding() {
        return false;
    }
}

