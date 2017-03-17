package finotek.global.dev.talkbank_ca.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.model.ChatItemSend;

public class SendViewBuilder implements ChatView.ViewBuilder<ChatItemSend> {
    private class SendViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        SendViewHolder(View itemView) {
            super(itemView);
            this.message = (TextView) itemView.findViewById(R.id.message);
        }
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_send, parent, false);
        return new SendViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, ChatItemSend data) {
        SendViewHolder holder = (SendViewHolder) viewHolder;
        holder.message.setText(data.getMessage());
    }
}
