package finotek.global.dev.talkbank_ca.chat.messages.control;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.scenario.LeftScenario;

public class RecommendScenarioMenuRequest extends RecoMenuRequest{
    public RecommendScenarioMenuRequest(Context context){
        if(!LeftScenario.scenarioList.isEmpty()) {
            setDescription(context.getResources().getString(R.string.main_string_cardif_recommend_title));

            for (String s : LeftScenario.scenarioList) {
                if (s.equals("E"))
                    addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_start_subscription), null);
            }
        } else {
            setDescription(context.getResources().getString(R.string.main_string_recommend_finished));
        }
    }
}
