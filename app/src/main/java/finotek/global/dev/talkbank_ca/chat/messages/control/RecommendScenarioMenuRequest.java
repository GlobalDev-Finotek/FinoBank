package finotek.global.dev.talkbank_ca.chat.messages.control;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.scenario.LeftScenario;
import finotek.global.dev.talkbank_ca.model.User;
import io.realm.Realm;

public class RecommendScenarioMenuRequest extends RecoMenuRequest {

	public RecommendScenarioMenuRequest(Context context) {
		if (!LeftScenario.scenarioList.isEmpty()) {
			Realm realm = Realm.getDefaultInstance();
			User user = realm.where(User.class).findAll().last();
			setDescription(context.getResources().getString(R.string.main_string_v2_greetings));


		} else {
			setDescription(context.getResources().getString(R.string.main_string_recommend_finished));
		}
	}
}
