package finotek.global.dev.brazil.chat.scenario;

import android.content.Context;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.MessageBox;
import finotek.global.dev.brazil.chat.messages.ReceiveMessage;
import finotek.global.dev.brazil.chat.messages.SendMessage;
import finotek.global.dev.brazil.chat.messages.action.Done;
import finotek.global.dev.brazil.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.brazil.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.brazil.model.User;
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
		return context.getString(R.string.main_string_v2_login_house_loan);
	}


	@Override
	public boolean decideOn(String msg) {
		return msg.equals(context.getResources().getString(R.string.main_string_v2_login_house_loan));

	}

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof Done) {
			MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_secured_mirocredit)));
		}
	}

	@Override
	public void onUserSend(String msg) {
		LeftScenario.scenarioList.remove("H");
		switch (step) {
			case Initial:
				step = Step.question;

				RecoMenuRequest req = new RecoMenuRequest();

				req.setDescription(context.getResources().getString(R.string.main_string_v2_login_house_recommend));
				req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_recommend_house_yes), null);
				req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.string_no), null);

				MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_house_ask)),
						req
				);
				break;
			case question:
				if (msg.equals(context.getResources().getString(R.string.string_no))) {
					MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_house_no_confirm, user.getName())),
							new RecommendScenarioMenuRequest(context));

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
		Initial, question, loan
	}
}
