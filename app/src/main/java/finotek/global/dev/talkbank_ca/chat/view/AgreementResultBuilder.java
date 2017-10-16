package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.action.ShowPdfView;
import finotek.global.dev.talkbank_ca.databinding.ChatAgreementResultBinding;
import finotek.global.dev.talkbank_ca.util.LocaleHelper;
import io.realm.Realm;

public class AgreementResultBuilder implements ChatView.ViewBuilder<Void> {
	private Context context;

	@Inject
	MyApplication application;

	public AgreementResultBuilder(Context context) {
		this.context = context;
	}

	@Override
	public RecyclerView.ViewHolder build(ViewGroup parent) {
		return new AgreementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_agreement_result, parent, false));
	}

	@Override
	public void bind(RecyclerView.ViewHolder viewHolder, Void data) {
	}

	@Override
	public void onDelete() {

	}

	private class AgreementViewHolder extends RecyclerView.ViewHolder {
		ChatAgreementResultBinding binding;

		AgreementViewHolder(View itemView) {
			super(itemView);
			binding = ChatAgreementResultBinding.bind(itemView);
			Context context = itemView.getContext();

			String loanText = context.getResources().getString(R.string.dialog_string_loan_service_user_agreement);
			String creditInfoText = context.getResources().getString(R.string.dialog_string_personal_credit_information_access_agreement);
			String loanTransactionText = context.getResources().getString(R.string.dialog_string_loan_transaction_agreement);
			String contractInformText = context.getResources().getString(R.string.dialog_string_contact_information);

			binding.btnLoanText.setText(String.format("%s.%s", loanText, "pdf"));
			binding.btnCreditInformText.setText(String.format("%s.%s", creditInfoText, "pdf"));
			binding.btnLoanTransactionText.setText(String.format("%s.%s", loanTransactionText, "pdf"));
			binding.btnContractInformText.setText(String.format("%s.%s", contractInformText, "pdf"));

			RxView.clicks(binding.btnLoanServicePreview)
					.throttleFirst(200, TimeUnit.MILLISECONDS)
					.subscribe(aVoid -> {
						String loan_service_pdf = (LocaleHelper.getLanguage(context).equals("ko")) ?  "view.pdf" : "view_eng.pdf";
						MessageBox.INSTANCE.add(new ShowPdfView(loanText, loan_service_pdf));
					});

			RxView.clicks(binding.bntLoanServiceSave)
					.throttleFirst(200, TimeUnit.MILLISECONDS)
					.subscribe(o -> {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("https://www.dropbox.com/s/ez3s0lk62pqx1ge/loan_service.pdf?dl=0"));
						context.startActivity(i);
					});

			RxView.clicks(binding.btnCreditInformPreview)
					.throttleFirst(200, TimeUnit.MILLISECONDS)
					.subscribe(aVoid -> {
						String credit_inform_pdf = (LocaleHelper.getLanguage(context).equals("ko")) ?  "view2.pdf" : "view2_eng.pdf";
						MessageBox.INSTANCE.add(new ShowPdfView(creditInfoText, credit_inform_pdf));
					});

			RxView.clicks(binding.btnCreditInforSave)
					.throttleFirst(200, TimeUnit.MILLISECONDS)
					.subscribe(o -> {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("https://www.dropbox.com/s/u9wsz2gn35eqfa1/credit_inform.pdf?dl=0"));
						context.startActivity(i);
					});

			RxView.clicks(binding.btnLoanTransactionPreview)
					.throttleFirst(200, TimeUnit.MILLISECONDS)
					.subscribe(aVoid -> {
						String loan_transaction_pdf = (LocaleHelper.getLanguage(context).equals("ko")) ?  "view3.pdf" : "view3_eng.pdf";
						MessageBox.INSTANCE.add(new ShowPdfView(loanTransactionText, loan_transaction_pdf));
					});

			RxView.clicks(binding.btnLoanTransactionSave)
					.throttleFirst(200, TimeUnit.MILLISECONDS)
					.subscribe(aVoid -> {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("https://www.dropbox.com/s/tbiyvrvqko959ul/loan_transaction.pdf?dl=0"));
						context.startActivity(i);
					});


			RxView.clicks(binding.btnContractInformPreview)
					.throttleFirst(200, TimeUnit.MILLISECONDS)
					.subscribe(aVoid -> {

						String contract_inform_pdf = (LocaleHelper.getLanguage(context).equals("ko")) ?  "view4.pdf" : "view4_eng.pdf";
						MessageBox.INSTANCE.add(new ShowPdfView(contractInformText, contract_inform_pdf));
					});

			RxView.clicks(binding.btnContractInformSave)
					.throttleFirst(200, TimeUnit.MILLISECONDS)
					.subscribe(aVoid -> {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("https://www.dropbox.com/s/d6155l9zjc53vm1/contract_inform.pdf?dl=0"));
						context.startActivity(i);
					});

		}
	}
}
