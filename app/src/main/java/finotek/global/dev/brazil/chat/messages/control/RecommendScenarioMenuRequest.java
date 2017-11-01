package finotek.global.dev.brazil.chat.messages.control;

import android.content.Context;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.scenario.LeftScenario;
import finotek.global.dev.brazil.model.User;
import io.realm.Realm;

public class RecommendScenarioMenuRequest extends RecoMenuRequest {

	public RecommendScenarioMenuRequest(Context context) {
		if (!LeftScenario.scenarioList.isEmpty()) {
			Realm realm = Realm.getDefaultInstance();
			User user = realm.where(User.class).findAll().last();
			setDescription(context.getResources().getString(R.string.brazil_scenario_greeting));
		} else {
			setDescription(context.getResources().getString(R.string.main_string_recommend_finished));
		}
	}
}
