package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.RequestKeyboardInput;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;

/**
 * Created by magyeong-ug on 04/07/2017.
 */

public class CPIScenario implements Scenario {
	private Context context;
	private Step step = Step.Agreement;

	public CPIScenario(Context context) {
		this.context = context;
	}

	@Override
	public String getName() {
		return context.getString(R.string.main_string_cardif_CPI_scenario);
	}


	@Override
	public boolean decideOn(String msg) {
		return msg.equals(context.getString(R.string.main_string_cardif_start_subscription));
	}

	@Override
	public void onReceive(Object msg) {


	}

	public RecoMenuRequest getRequestConfirm() {
		RecoMenuRequest req = new RecoMenuRequest();
		//req.setTitle("추천메뉴");
		req.setDescription(context.getResources().getString(R.string.main_string_cardif_CPI_agreement_desc));

		req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_cardif_CPI_agree), null);
		req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.main_string_cardif_CPI_disagree), null);
		return req;
	}

	@Override
	public void onUserSend(String msg) {
		LeftScenario.scenarioList.remove("E");
		switch (step) {

			case Agreement:
				MessageBox.INSTANCE.addAndWait(
						getRequestConfirm()
				);
				step = Step.Name;
				break;
			case Name:
				if (msg.equals(context.getResources().getString(R.string.main_string_cardif_CPI_agree))) {
					MessageBox.INSTANCE.addAndWait(
							new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_name)),
							new RequestKeyboardInput()
					);
				} else if (msg.equals(context.getResources().getString(R.string.main_string_cardif_CPI_disagree))) {
					MessageBox.INSTANCE.addAndWait(
							new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_microedit_cancel)),
							new Done()
					);
				}
				step = Step.Birth;
				break;

			case Birth:
				MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getString(R.string.main_string_cardif_CPI_birth)));
				step = Step.Doctor;
				break;
			case Doctor:
				MessageBox.INSTANCE.addAndWait(
						RecoMenuRequest.buildYesOrNo(context, context.getResources().getString(R.string.main_string_cardif_CPI_doctor))
				);
				MessageBox.INSTANCE.add(
						new Done()
				);
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
		Initial, Agreement, Birth, Doctor, Name
	}
}
