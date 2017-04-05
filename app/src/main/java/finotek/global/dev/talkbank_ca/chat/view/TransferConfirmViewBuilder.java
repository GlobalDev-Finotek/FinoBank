package finotek.global.dev.talkbank_ca.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.extensions.ChatSelectButtonEvent;
import finotek.global.dev.talkbank_ca.widget.RoundButton;

public class TransferConfirmViewBuilder implements  ChatView.ViewBuilder<ChatSelectButtonEvent> {
    class ViewHolder extends RecyclerView.ViewHolder {
        RoundButton confirmButton;
        RoundButton cancelButton;
        RoundButton transferOtherButton;

        public ViewHolder(View itemView) {
            super(itemView);
            confirmButton = (RoundButton) itemView.findViewById(R.id.confirm_btn);
            cancelButton = (RoundButton) itemView.findViewById(R.id.cancel_btn);
            transferOtherButton = (RoundButton) itemView.findViewById(R.id.transfer_other_btn);
        }
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_confirm_transfer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, ChatSelectButtonEvent data) {
        ViewHolder holder = (ViewHolder) viewHolder;

        RxView.clicks(holder.confirmButton)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(data.getConfirmAction());

        RxView.clicks(holder.cancelButton)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(data.getCancelAction());

        RxView.clicks(holder.transferOtherButton)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(data.getTransferOtherAction());
    }

    @Override
    public void onDelete() {

    }
}
