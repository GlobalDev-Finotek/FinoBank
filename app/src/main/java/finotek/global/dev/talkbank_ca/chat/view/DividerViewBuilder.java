package finotek.global.dev.talkbank_ca.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.DividerMessage;

public class DividerViewBuilder implements ChatView.ViewBuilder<DividerMessage> {
    private class DividerViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        DividerViewHolder(View itemView) {
            super(itemView);
            this.message = (TextView) itemView.findViewById(R.id.message);
        }
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_divider, parent, false);
        return new DividerViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, DividerMessage data) {
        DividerViewHolder holder = (DividerViewHolder) viewHolder;
        holder.message.setText(data.getMessage());
    }

    @Override
    public void onDelete() {

    }
}
