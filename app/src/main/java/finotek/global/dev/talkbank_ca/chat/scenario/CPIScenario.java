package finotek.global.dev.talkbank_ca.chat.scenario;

import android.content.Context;
import android.text.InputType;

import java.util.ArrayList;
import java.util.List;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.Agreement;
import finotek.global.dev.talkbank_ca.chat.messages.AgreementRequest;
import finotek.global.dev.talkbank_ca.chat.messages.ImageMessage;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.DismissKeyboard;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.RequestKeyboardInput;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.cpi.CPIAuthenticationDone;
import finotek.global.dev.talkbank_ca.chat.messages.cpi.CPIAuthenticationWait;
import finotek.global.dev.talkbank_ca.chat.messages.cpi.CPIContractIsDone;
import finotek.global.dev.talkbank_ca.chat.messages.cpi.RemoteCallDone;
import finotek.global.dev.talkbank_ca.chat.messages.cpi.RequestRemoteCall;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI;
import finotek.global.dev.talkbank_ca.chat.messages.ui.IDCardInfo;
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
		if(msg instanceof IDCardInfo) {
			RecoMenuRequest req = new RecoMenuRequest();
			req.setDescription(context.getResources().getString(R.string.main_string_cardif_CPI_select_authenticate_on_remote_call));
			req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_cardif_CPI_authenticate_on_remote_call), () -> {
				MessageBox.INSTANCE.add(new RequestRemoteCall());
			});
			MessageBox.INSTANCE.addAndWait(req);
			step = Step.IdentificationProcess;
		}

		if(msg instanceof RemoteCallDone) {
			RecoMenuRequest req = new RecoMenuRequest();
			req.setDescription(context.getResources().getString(R.string.main_string_cardif_CPI_select_sign_on_contract));
			req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_cardif_CPI_sign_on_contract), () -> {
				MessageBox.INSTANCE.add(new RequestSignature());
			});
			MessageBox.INSTANCE.addAndWait(req);
			step = Step.SignatureProcess;
		}

        if(msg instanceof CPIContractIsDone) {
            MessageBox.INSTANCE.addAndWait(
                new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_subscription_completed)),
                new Done()
            );
        }
	}

	public RecoMenuRequest getRequestConfirm(int desc) {
		RecoMenuRequest req = new RecoMenuRequest();
		req.setDescription(context.getResources().getString(desc));

		req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_cardif_CPI_agree), null);
		req.addMenu(R.drawable.icon_sad, context.getResources().getString(R.string.main_string_cardif_CPI_disagree), () -> {

        }, true);
		return req;
	}

	@Override
	public void onUserSend(String msg) {
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
				step = Step.SelectMyLoan;
				break;
			case SelectMyLoan: {
				RecoMenuRequest req = new RecoMenuRequest();
				req.setDescription(context.getResources().getString(R.string.main_string_cardif_CPI_select_loan_desc));
				req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_loan1), () -> {
					MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_cardif_CPI_loan1_answer)));
				});
				req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_loan2), () -> {
					MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_cardif_CPI_loan2_answer)));
				});
				req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_loan3), () -> {
					MessageBox.INSTANCE.add(new SendMessage(context.getResources().getString(R.string.main_string_cardif_CPI_loan3_answer)));
				});
				MessageBox.INSTANCE.addAndWait(new DismissKeyboard(), req);

				step = Step.TypeInsuranceAmount;
				}
				break;
			case TypeInsuranceAmount:
				MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_type_insurance_amount)),
					new RequestTransferUI()
				);
				step = Step.TypeInsurancePeriod;
				break;
			case TypeInsurancePeriod:
				MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_type_insurance_period)),
					new RequestKeyboardInput(InputType.TYPE_CLASS_PHONE)
				);
				step = Step.SelectOption;
				break;
			case SelectOption: {
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

				step = Step.UserSelectedOption;
				}
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
					MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_payment_bank_name)));
					step = Step.TypePaymentBankName;
				}
				break;
			case TypePaymentBankName:
				MessageBox.INSTANCE.addAndWait(new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_payment_bank_account)));
				step = Step.TypePaymentBankAccount;
				break;
			case TypePaymentBankAccount:
				MessageBox.INSTANCE.addAndWait(
					new ReceiveMessage(context.getResources().getString(R.string.main_string_cardif_CPI_payment_date)),
					new RequestKeyboardInput(InputType.TYPE_CLASS_PHONE)
				);
				step = Step.TypePaymentDate;
				break;
			case TypePaymentDate: {
					RecoMenuRequest req = new RecoMenuRequest();
					req.setDescription(context.getResources().getString(R.string.main_string_cardif_CPI_select_identification));
					req.addMenu(R.drawable.icon_haha, context.getResources().getString(R.string.main_string_cardif_CPI_start_identification), () -> {
						MessageBox.INSTANCE.add(new RequestTakeIDCard());
					});
					MessageBox.INSTANCE.addAndWait(
						new DismissKeyboard(),
						req
					);
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
                        req.addMenu(R.drawable.icon_like, context.getResources().getString(R.string.main_string_cardif_CPI_option4), () -> {

                        }, true);
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

	private void selectUser(String name) {
        MessageBox.INSTANCE.addAndWait(
            new ReceiveMessage("You selected " + name + ", Please type the password."),
            new RequestKeyboardInput(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
        );

        step = Step.AuthenticationPassword;
    }

	private enum Step {
		Initial, Agreement, Name, Birth, SelectMyLoan, TypeInsuranceAmount, TypeInsurancePeriod,
		SelectOption, UserSelectedOption,
		TypePaymentBankName, TypePaymentBankAccount, TypePaymentDate,
		Authentication, AuthenticationPassword,
		MoreInformation, MoreInformationAnswer,
		IdentificationProcess, SignatureProcess
	}
}
