package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import org.joda.time.DateTime;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.AccountConfirm;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RemoteCallCompleted;
import finotek.global.dev.talkbank_ca.chat.messages.RequestRemoteCall;
import finotek.global.dev.talkbank_ca.chat.messages.RequestTakeAnotherIDCard;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignatureRegister;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignatureValidation;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestTakeIDCard;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.user.util.AccountImageBuilder;
import finotek.global.dev.talkbank_ca.util.LocaleHelper;
import io.realm.Realm;

/**
 * Created by idohyeon on 2017. 10. 26..
 */

public class TrySignScenario implements Scenario {
    private Context context;
    private String yes = "";
    private String no = "";

    public TrySignScenario(Context context) {
        this.context = context;
        yes = context.getString(R.string.string_yes);
        no = context.getString(R.string.string_no);
    }

    @Override
    public String getName() {
        return context.getString(R.string.scenario_account);
    }

    @Override
    public boolean decideOn(String msg) {
        Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findAll().last();
        return msg.equals("ㅅㅁㅇㅈ") || msg.equals("Verificar a assinatura do ".toLowerCase() + user.getName() + " por favor".toLowerCase());
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof SignatureVerified) {
            MessageBox.INSTANCE.addAndWait(
                new ReceiveMessage("É correto sua assinatura escondida. A verificação está completa."),
                new Done()
            );
        }
    }

    @Override
    public void onUserSend(String msg) {
        MessageBox.INSTANCE.addAndWait(new RequestSignatureValidation());
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isProceeding() {
        return true;
    }
}