package finotek.global.dev.talkbank_ca.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ShowContract;
import finotek.global.dev.talkbank_ca.chat.messages.action.ShowPdfView;

public class ShowContractViewBuilder implements ChatView.ViewBuilder<ShowContract> {
    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_contract_button, parent, false);
        view.setTag(ShowContractViewBuilder.ContractViewHolder.class.getName());
        return new ShowContractViewBuilder.ContractViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, ShowContract data) {
        ShowContractViewBuilder.ContractViewHolder holder = (ShowContractViewBuilder.ContractViewHolder) viewHolder;
        if(data.getStep() == 2) {
            holder.showContractButton.setText("Show Signed Contract");
        }

        holder.showContractButton.setOnClickListener((v) -> {
            MessageBox.INSTANCE.addAndWait(new ShowPdfView("Contract", "contract.pdf", data.getStep()));
        });
    }

    @Override
    public void onDelete() {

    }

    public class ContractViewHolder extends RecyclerView.ViewHolder {
        public Button showContractButton;

        ContractViewHolder(View itemView) {
            super(itemView);
            showContractButton = (Button) itemView.findViewById(R.id.show_contract_button);
        }
    }
}

