package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;
import android.text.InputType;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.DismissKeyboard;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.RequestKeyboardInput;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.cpi.CPIAuthenticationDone;
import finotek.global.dev.talkbank_ca.chat.messages.cpi.CPIAuthenticationWait;
import finotek.global.dev.talkbank_ca.chat.messages.ui.IDCardShown;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestTakeIDCard;

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
		if(msg instanceof IDCardShown) {
			RecoMenuRequest req = new RecoMenuRequest();
			req.setDescription(context.getResources().getString(R.string.main_string_cardif_CPI_select_sign_on_contract));
			req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_cardif_CPI_sign_on_contract), () -> {
				MessageBox.INSTANCE.add(new RequestSignature());
			});
            MessageBox.INSTANCE.addAndWait(req);
			step = Step.SignatureProcess;
		}

		if(msg instanceof CPIAuthenticationDone) {
            RecoMenuRequest req = new RecoMenuRequest();
            req.setDescription(context.getResources().getString(R.string.main_string_cardif_CPI_select_option_desc));
            req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_option1), () -> {
                MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_cardif_CPI_option1_answer)));
            });
            req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_option2), () -> {
                MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_cardif_CPI_option2_answer)));
            });
            req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_option3), () -> {
                MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_cardif_CPI_option3_answer)));
            });
            req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_option4), () -> {
                MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_cardif_CPI_option4)));
            });
            MessageBox.INSTANCE.addAndWait(new DismissKeyboard(), req);

            step = Step.UserSelectedOption;            // 사용자가 옵션을 선택함
        }
	}

	public RecoMenuRequest getRequestConfirm(int desc) {
		RecoMenuRequest req = new RecoMenuRequest();
		//req.setTitle("추천메뉴");
		req.setDescription(context.getResources().getString(desc));

		req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_cardif_CPI_agree), null);
		req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.main_string_cardif_CPI_disagree), null);
		return req;
	}

	@Override
	public void onUserSend(String msg) {
		LeftScenario.scenarioList.remove("E");
		switch (step) {

			case Agreement:
				MessageBox.INSTANCE.addAndWait(getRequestConfirm(R.string.main_string_cardif_CPI_agreement_desc));
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
							new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_subscription_canceled)),
							new Done()
					);
				}
				step = Step.Birth;
				break;
			case Birth:
				MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage(context.getString(R.string.main_string_cardif_CPI_birth)),
					new DismissKeyboard(),
                    new RequestKeyboardInput(InputType.TYPE_CLASS_PHONE)
				);
				step = Step.Doctor;
				break;
			case Doctor:
				MessageBox.INSTANCE.addAndWait(
                    new DismissKeyboard(),
					RecoMenuRequest.buildYesOrNo(context, context.getResources().getString(R.string.main_string_cardif_CPI_doctor))
				);
				step = Step.LoanQuestion;
				break;
			case LoanQuestion:
				MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_loan_question)),
					new RequestKeyboardInput(InputType.TYPE_CLASS_PHONE)
				);
				step = Step.PeriodQuestion;
				break;
			case PeriodQuestion:
				MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_period_question)));
				step = Step.DateQuestion;
				break;
			case DateQuestion:
				MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_date_question)));
				step = Step.AgreeToCheck;
				break;
            case AgreeToCheck:
                MessageBox.INSTANCE.addAndWait(
                    new DismissKeyboard(),
                    getRequestConfirm(R.string.main_string_cardif_CPI_agree_to_check_desc)
                );
                step = Step.Authentication;
                break;
            case Authentication:
                if (msg.equals(context.getResources().getString(R.string.main_string_cardif_CPI_agree))) {
                    RecoMenuRequest req = new RecoMenuRequest();
                    Runnable listener = () -> {
                        MessageBox.INSTANCE.add(new CPIAuthenticationWait());
                    };

                    req.setDescription(context.getResources().getString(R.string.main_string_cardif_CPI_please_select_authentication_certificate));
                    req.addMenu(R.drawable.icon_haha, "Kim Sung Won\nexpired at: 2017-08-08\ntype: personal usage", listener);
                    req.addMenu(R.drawable.icon_haha, "Kim Woo Seob\nexpired at: 2017-11-09\ntype: personal usage", listener);
                    req.addMenu(R.drawable.icon_haha, "Lee Kyung Jin\nexpired at: 2017-09-25\ntype: personal usage (banking)", listener);
                    MessageBox.INSTANCE.addAndWait(req);
                } else if (msg.equals(context.getResources().getString(R.string.main_string_cardif_CPI_disagree))) {
                    MessageBox.INSTANCE.addAndWait(
                            new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_subscription_canceled)),
                            new Done()
                    );
                }
                step = Step.SelectOption;
                break;
			case UserSelectedOption:
				if (msg.equals(context.getResources().getString(R.string.main_string_cardif_CPI_option4))) {
					MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_any_help)));
					step = Step.MoreInformation;
				} else if(msg.equals(context.getResources().getString(R.string.main_string_cardif_CPI_cancel_option))){
					MessageBox.INSTANCE.addAndWait(
						new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_subscription_canceled)),
						new Done()
					);
				} else {
					RecoMenuRequest req = new RecoMenuRequest();
					req.setDescription(context.getResources().getString(R.string.main_string_cardif_CPI_select_identification));
					req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_cardif_CPI_start_identification), () -> {
						MessageBox.INSTANCE.add(new RequestTakeIDCard());
					});
                    MessageBox.INSTANCE.addAndWait(req);
					step = Step.IdentificationProcess;
				}
				break;
			case MoreInformation: {
					RecoMenuRequest req = new RecoMenuRequest();
					req.setDescription(context.getResources().getString(R.string.main_string_cardif_CPI_more_answered_desc));
					req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_more_answered_option1), null);
					req.addMenu(R.drawable.icon_love, context.getResources().getString(R.string.main_string_cardif_CPI_more_answered_option2), null);
					MessageBox.INSTANCE.addAndWait(new DismissKeyboard(), req);

					step = Step.MoreInformationAnswer;
				}
				break;
			case MoreInformationAnswer: {
					if(msg.equals(context.getResources().getString(R.string.main_string_cardif_CPI_more_answered_option1))) {
						MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_any_help)));

						step = Step.MoreInformation;
					} else if(msg.equals(context.getResources().getString(R.string.main_string_cardif_CPI_more_answered_option2))) {
						RecoMenuRequest req = new RecoMenuRequest();
						req.setDescription(context.getResources().getString(R.string.main_string_cardif_CPI_select_option_desc));
						req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_option1), () -> {
							MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_cardif_CPI_option1_answer)));
						});
						req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_option2), () -> {
							MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_cardif_CPI_option2_answer)));
						});
						req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_option3), () -> {
							MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_cardif_CPI_option3_answer)));
						});
						req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_cancel_option), null);
						MessageBox.INSTANCE.addAndWait(new DismissKeyboard(), req);

						step = Step.UserSelectedOption;
					}
				}
				break;
		}
	}

	@Override
	public void clear() {
        step = Step.Agreement;
	}

	@Override
	public boolean isProceeding() {
		return false;
	}

	private enum Step {
		Initial, Agreement, Birth, Name,
		Doctor, LoanQuestion, PeriodQuestion, DateQuestion, AgreeToCheck,
		Authentication, SelectOption, UserSelectedOption,
		MoreInformation, MoreInformationAnswer,
		IdentificationProcess, SignatureProcess
	}
}
