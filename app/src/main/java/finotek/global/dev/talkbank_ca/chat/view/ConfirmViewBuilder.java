package finotek.global.dev.talkbank_ca.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.adapter.ChatSelectButtonEvent;
import finotek.global.dev.talkbank_ca.widget.RoundButton;

public class ConfirmViewBuilder implements  ChatView.ViewBuilder<ChatSelectButtonEvent> {
    class ConfirmViewHolder extends RecyclerView.ViewHolder {
        RoundButton confirmButton;
        RoundButton cancelButton;

        public ConfirmViewHolder(View itemView) {
            super(itemView);
            confirmButton = (RoundButton) itemView.findViewById(R.id.confirm_btn);
            cancelButton = (RoundButton) itemView.findViewById(R.id.cancel_btn);
        }
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_confirm, parent, false);
        return new ConfirmViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, ChatSelectButtonEvent data) {
        ConfirmViewHolder holder = (ConfirmViewHolder) viewHolder;

        RxView.clicks(holder.confirmButton)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(data.getConfirmAction());

        RxView.clicks(holder.cancelButton)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(data.getCancelAction());
    }

    @Override
    public void onDelete() {

    }
}