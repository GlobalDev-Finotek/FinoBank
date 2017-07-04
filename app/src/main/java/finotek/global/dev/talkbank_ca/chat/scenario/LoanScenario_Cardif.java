package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;

import org.joda.time.DateTime;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementResult;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RequestUserInformation;
import finotek.global.dev.talkbank_ca.chat.messages.Transaction;
import finotek.global.dev.talkbank_ca.chat.messages.action.DismissKeyboard;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.RequestKeyboardInput;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;

public class LoanScenario_Cardif implements Scenario {
	private Context context;
	private Step step = Step.Initial;

	public LoanScenario_Cardif(Context context) {
		this.context = context;
	}

	@Override
	public String getName() {
		return context.getString(R.string.scenario_loan);
	}

	@Override
	public boolean decideOn(String msg) {
		// TODO msg 변경
		if (msg.equals("loan"))
			return true;
		switch (msg) {
			case "집을 담보로 대출 받고 싶어":
			case "소액 담보 대출":
			case "소액 담보대출":
			case "소액담보 대출":
			case "소액담보대출":
			case "담보 대출":
			case "담보대출":
			case "대출":
				return true;
			default:
				return msg.equals(context.getResources().getString(R.string.main_string_secured_mirocredit));
		}
	}

	// SendMessage를 제외한 모든 메시지를 받음
	@Override
	public void onReceive(Object msg) {
		if (msg instanceof SignatureVerified) {
			if (step == Step.Last) {
				// 입금내역 추가
				TransactionDB.INSTANCE.deposit(50000000);
				TransactionDB.INSTANCE.addTx(
						new Transaction(context.getString(R.string.main_string_secured_mirocredit),
								1, 50000000, TransactionDB.INSTANCE.getBalance(), new DateTime()
						));

				MessageBox.INSTANCE.add(new RequestRemoveControls());
				MessageBox.INSTANCE.addAndWait(
					new AgreementResult(),
					new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_loan_success)),
					new Done()
				);
			}
		}

		if (msg instanceof Done) {
			MessageBox.INSTANCE.add(new RecommendScenarioMenuRequest(context));
			step = Step.Initial;
		}
	}

	// 사용자가 톡뱅에게 메시지를 보낼 때 호출되는 이벤트
	@Override
	public void onUserSend(String msg) {
		switch (step) {
			case Initial:

				MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.dialog_string_cardif_loan_amount)),
						new RequestKeyboardInput()
				);
				step = Step.InputPeriod;
				break;

			case InputPeriod:
					MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.dialog_string_cardif_loan_period)));
					step = Step.InputStartDate;
				break;
			case InputStartDate:
				MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.dialog_string_cardif_loan_startdate))

				);
				step = Step.Agreement;
				break;
			case Agreement:
				RecoMenuRequest req = new RecoMenuRequest();

				req.setDescription(context.getResources().getString(R.string.dialog_string_cardif_loan_agree));
				req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.string_agree), null);
				req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.string_disagree), null);

				MessageBox.INSTANCE.addAndWait(
						req,
						new DismissKeyboard()
				);

				step = Step.SelectCerti;
				break;

			case SelectCerti:
				MessageBox.INSTANCE.add(new RequestUserInformation());

				RecoMenuRequest recoMenuRequest = new RecoMenuRequest();

				recoMenuRequest.setDescription(context.getResources().getString(R.string.main_string_v2_login_house_recommend));
				recoMenuRequest.addMenu(R.drawable.icon_haha, "Sen Park", null);
				recoMenuRequest.addMenu(R.drawable.icon_sad, "Sen Park", null);
				recoMenuRequest.addMenu(R.drawable.icon_sad, "Sen Park", null);

				MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.main_string_v2_login_house_ask)),
						recoMenuRequest
				);
				step = Step.SelectLoan;
				break;

			case SelectLoan:
				RecoMenuRequest loanRecMenu = new RecoMenuRequest();

				loanRecMenu.setDescription(context.getResources().getString(R.string.dialog_chat_loan_select));
				loanRecMenu.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_cardif_loan_opt1), null);
				loanRecMenu.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_cardif_loan_opt2), null);
				loanRecMenu.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_cardif_loan_opt3), null);
				loanRecMenu.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.dialog_chat_cardif_loan_opt4), null);

				MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_loan_select)),
						loanRecMenu
				);

				step = Step.Inform;
				break;

			case Inform:
				RecoMenuRequest loanInformMenu = new RecoMenuRequest();

				loanInformMenu.setDescription(context.getResources().getString(R.string.dialog_chat_loan_inform));
				loanInformMenu.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_cardif_loan_more_inform), null);
				loanInformMenu.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.dialog_chat_cardif_loan_resume_subscription), null);

				MessageBox.INSTANCE.addAndWait(
						loanInformMenu
				);

				step = Step.SelectLoan;

				break;

			case Sign:
				break;
			case Last:
				break;
			default:
				//MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.dialog_chat_recognize_error)));
				MessageBox.INSTANCE.addAndWait(new RecommendScenarioMenuRequest(context));
				break;
		}
	}

	@Override
	public void clear() {
		step = Step.Initial;
	}

	@Override
	public boolean isProceeding() {
		return true;
	}

	private enum Step {
		Initial, InputPeriod, InputStartDate, SelectCerti, Agreement, SelectLoan, Inform, Sign, Last
	}
}
