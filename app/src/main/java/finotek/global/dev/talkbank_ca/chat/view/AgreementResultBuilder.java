package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.action.ShowPdfView;
import finotek.global.dev.talkbank_ca.databinding.ChatAgreementResultBinding;

public class AgreementResultBuilder implements ChatView.ViewBuilder<Void> {
	@Inject
	MyApplication application;

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
						MessageBox.INSTANCE.add(new ShowPdfView(loanText, "out1.png"));
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
						MessageBox.INSTANCE.add(new ShowPdfView(creditInfoText, "out2.png"));
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
						MessageBox.INSTANCE.add(new ShowPdfView(loanTransactionText, "out3.png"));
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
						MessageBox.INSTANCE.add(new ShowPdfView(contractInformText, "out4.png"));
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
