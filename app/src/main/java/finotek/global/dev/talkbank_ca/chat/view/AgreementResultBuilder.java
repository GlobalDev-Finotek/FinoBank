package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.action.ShowPdfView;
import finotek.global.dev.talkbank_ca.databinding.ChatAgreementResultBinding;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AgreementResultBuilder implements ChatView.ViewBuilder<Void> {
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
                .throttleFirst(2000, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    MessageBox.INSTANCE.add(new ShowPdfView(loanText, "loan_service.pdf"));
                });

            RxView.clicks(binding.btnCreditInformPreview)
                .throttleFirst(2000, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    MessageBox.INSTANCE.add(new ShowPdfView(creditInfoText, "credit_inform.pdf"));
                });

            RxView.clicks(binding.btnLoanTransactionPreview)
                .throttleFirst(2000, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    MessageBox.INSTANCE.add(new ShowPdfView(loanTransactionText, "credit_inform.pdf"));
                });

            RxView.clicks(binding.btnContractInformPreview)
                .throttleFirst(2000, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    MessageBox.INSTANCE.add(new ShowPdfView(contractInformText, "credit_inform.pdf"));
                });
        }
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
}
