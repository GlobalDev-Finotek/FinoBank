package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AgreementResultBuilder implements ChatView.ViewBuilder<Void> {

	@Inject
	MyApplication application;

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        return new AgreementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_agreement_result, parent, false));
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Void data) {
        AgreementViewHolder vh = (AgreementViewHolder) viewHolder;

        vh.btnContractInformPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://drive.google.com/open?id=0B0uqV2k-AfoWMndOc1V4MFRSVUE"));
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
	            PackageManager pm = application.getContext().getPackageManager();
	            List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
	            if (activities.size() > 0) {
		            application.getContext().startActivity(intent);
	            } else {
                    // Do something else here. Maybe pop up a Dialog or Toast
                }
            }
        });

        vh.btnCreditInformPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://drive.google.com/open?id=0B0uqV2k-AfoWWG1IZVhVTU9ET00"));
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
	            application.getContext().startActivity(intent);
            }
        });


        vh.btnLoanServicePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://drive.google.com/open?id=0B0uqV2k-AfoWRFpEZnFCZGtLUjA"));
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
	            PackageManager pm = application.getContext().getPackageManager();
	            List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
	            if (activities.size() > 0) {
		            application.getContext().startActivity(intent);
	            } else {
                    // Do something else here. Maybe pop up a Dialog or Toast
                }
            }
        });

        vh.btnLoanTransactionPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://drive.google.com/open?id=0B0uqV2k-AfoWZ2lLcVRMd2IycW8"));
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
	            PackageManager pm = application.getContext().getPackageManager();
	            List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
	            if (activities.size() > 0) {
		            application.getContext().startActivity(intent);
	            } else {
                    // Do something else here. Maybe pop up a Dialog or Toast
                }
            }
        });
    }

    @Override
    public void onDelete() {

    }

	private class AgreementViewHolder extends RecyclerView.ViewHolder {
		Button btnLoanServicePreview;
		Button btnCreditInformPreview;
		Button btnLoanTransactionPreview;
		Button btnContractInformPreview;

		AgreementViewHolder(View itemView) {
			super(itemView);
			btnLoanServicePreview = (Button) itemView.findViewById(R.id.btn_loan_service_preview);
			btnCreditInformPreview = (Button) itemView.findViewById(R.id.btn_credit_inform_preview);
			btnLoanTransactionPreview = (Button) itemView.findViewById(R.id.btn_loan_transaction_preview);
			btnContractInformPreview = (Button) itemView.findViewById(R.id.btn_contract_inform_preview);
		}
	}
}
