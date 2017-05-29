package finotek.global.dev.talkbank_ca.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.WaitResult;

public class WaitViewBuilder implements ChatView.ViewBuilder<Void> {
    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_wait, parent, false);
        return new WaitViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Void data) {
        // nothing to do
    }

    @Override
    public void onDelete() {

    }

    private class WaitViewHolder extends RecyclerView.ViewHolder {
        WaitViewHolder(View itemView) {
            super(itemView);
        }
    }
}