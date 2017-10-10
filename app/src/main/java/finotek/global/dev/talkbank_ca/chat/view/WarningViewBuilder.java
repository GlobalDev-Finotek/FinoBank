package finotek.global.dev.talkbank_ca.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.WarningMessage;

public class WarningViewBuilder implements ChatView.ViewBuilder<WarningMessage> {
    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_warning, parent, false);
        return new WarningViewBuilder.WarningViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, WarningMessage data) {
        WarningViewBuilder.WarningViewHolder holder = (WarningViewBuilder.WarningViewHolder) viewHolder;
        holder.message.setText(data.getMessage());
    }

    @Override
    public void onDelete() {

    }

    private class WarningViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        WarningViewHolder(View itemView) {
            super(itemView);
            this.message = (TextView) itemView.findViewById(R.id.message);
        }
    }
}
