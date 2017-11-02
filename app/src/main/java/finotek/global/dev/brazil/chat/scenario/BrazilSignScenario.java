package finotek.global.dev.brazil.chat.scenario;

import android.content.Context;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.MessageBox;
import finotek.global.dev.brazil.chat.messages.ReceiveMessage;
import finotek.global.dev.brazil.chat.messages.action.Done;
import finotek.global.dev.brazil.chat.messages.action.SignatureFailed;
import finotek.global.dev.brazil.chat.messages.action.SignatureVerified;
import finotek.global.dev.brazil.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.brazil.chat.messages.ui.IDCardInfo;
import finotek.global.dev.brazil.chat.messages.ui.RequestSignatureValidation;
import finotek.global.dev.brazil.model.User;
import io.realm.Realm;

/**
 * Created by idohyeon on 2017. 10. 26..
 */

public class BrazilSignScenario implements Scenario {
    private Context context;
    private Step step;

    public BrazilSignScenario(Context context) {
        this.context = context;
        this.step = Step.initial;
    }

    @Override
    public String getName() {
        return "sign Scenario";
    }

    @Override
    public boolean decideOn(String msg) {
        Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findAll().last();
        return msg.equals("ㅅㅁㅇㅈ") || msg.equals("Verificar a assinatura do ".toLowerCase() + user.getName() + " por favor".toLowerCase())
                || msg.equals(context.getResources().getString(R.string.brazil_scenario_request_ronaldo_sign));
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof SignatureVerified) {
            MessageBox.INSTANCE.addAndWait(
                new ReceiveMessage(context.getResources().getString(R.string.brazil_scenario_signature_verified)),
                new Done()
            );
        }

        if (msg instanceof SignatureFailed) {
            MessageBox.INSTANCE.addAndWait(
                    new ReceiveMessage(context.getResources().getString(R.string.brazil_scenario_try_again)),
                    new Done()
            );
        }

        if (msg instanceof Done) {
            step = Step.initial;
        }
    }

    @Override
    public void onUserSend(String msg) {
        switch (step) {
            case initial:
                MessageBox.INSTANCE.addAndWait(
                    new IDCardInfo(context.getString(R.string.default_user_name), "02.05.1985"),
                    RecommendScenarioMenuRequest.buildYesOrNo(context, context.getResources().getString(R.string.brazil_scenario_ronaldo_id))
                );
                step = Step.receiveSign;
                break;
            case receiveSign:
                if (msg.equals(context.getResources().getString(R.string.dialog_button_yes))) {
                    MessageBox.INSTANCE.addAndWait(
                            new ReceiveMessage(context.getResources().getString(R.string.brazil_scenario_ronaldo_signature)),
                            new RequestSignatureValidation()
                    );
                } else if (msg.equals(context.getResources().getString(R.string.dialog_button_no))) {
                    MessageBox.INSTANCE.addAndWait(
                            new ReceiveMessage(context.getResources().getString(R.string.brazil_scenario_try_again)),
                            new Done()
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
        return true;
    }

    private enum Step {
        initial, receiveSign, receiveHiddenSign, finished
    }
}