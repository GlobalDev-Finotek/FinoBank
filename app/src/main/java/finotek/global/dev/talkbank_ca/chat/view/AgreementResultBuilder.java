package finotek.global.dev.talkbank_ca.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import finotek.global.dev.talkbank_ca.R;

public class AgreementResultBuilder implements ChatView.ViewBuilder<Void> {
    private class AgreementViewHolder extends RecyclerView.ViewHolder {
        AgreementViewHolder(View itemView) {
            super(itemView);
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
