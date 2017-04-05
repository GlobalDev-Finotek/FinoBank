package finotek.global.dev.talkbank_ca.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;

public class ReceiveViewBuilder implements ChatView.ViewBuilder<ReceiveMessage> {
    private class ReceiveViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        ReceiveViewHolder(View itemView) {
            super(itemView);
            this.message = (TextView) itemView.findViewById(R.id.message);
        }
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_receive, parent, false);
        return new ReceiveViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, ReceiveMessage data) {
        ReceiveViewHolder holder = (ReceiveViewHolder) viewHolder;
        holder.message.setText(data.getMessage());
    }

    @Override
    public void onDelete() {

    }
}