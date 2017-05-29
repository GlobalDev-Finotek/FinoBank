package finotek.global.dev.talkbank_ca.chat.messages.control;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.scenario.LeftScenario;
import finotek.global.dev.talkbank_ca.model.User;
import io.realm.Realm;

public class RecommendScenarioMenuRequest extends RecoMenuRequest{

    public RecommendScenarioMenuRequest(Context context){
        if(!LeftScenario.scenarioList.isEmpty()) {
            Realm realm = Realm.getDefaultInstance();
            User user = realm.where(User.class).findAll().last();
            setTitle(context.getResources().getString(R.string.main_string_recommend_menu));
            setDescription(context.getResources().getString(R.string.main_string_v2_login_recommend_task, user.getName()));

            for (String s : LeftScenario.scenarioList) {
                if (s.equals("E"))
                    addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_recommend_electric_title), null);
                if (s.equals("P"))
                    addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_recommend_parents_title), null);
                if (s.equals("T"))
                    addMenu(R.drawable.icon_love, context.getResources().getString(R.string.main_string_v2_login_open_saving_account), null);
                if (s.equals("H"))
                    addMenu(R.drawable.icon_love, context.getResources().getString(R.string.main_string_v2_login_house_loan), null);
            }
            addMenu(R.drawable.icon_wow, context.getResources().getString(R.string.main_string_v2_login_notify_again), null);
        }
        else{
            setDescription(context.getResources().getString(R.string.main_string_recommend_finished));

        }
    }
}
