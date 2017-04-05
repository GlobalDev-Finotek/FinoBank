package finotek.global.dev.talkbank_ca.chat.view;

import android.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmControlEvent;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.util.Converter;
import finotek.global.dev.talkbank_ca.widget.RoundButton;

public class ConfirmViewBuilder implements  ChatView.ViewBuilder<ConfirmRequest> {
    class ConfirmViewHolder extends RecyclerView.ViewHolder {
        LinearLayout buttons;

        public ConfirmViewHolder(View itemView) {
            super(itemView);
            buttons = (LinearLayout) itemView.findViewById(R.id.buttons);
        }
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_confirm, parent, false);
        return new ConfirmViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, ConfirmRequest data) {
        ConfirmViewHolder holder = (ConfirmViewHolder) viewHolder;
        holder.buttons.removeAllViews();
        List<ConfirmControlEvent> events = data.getEvents();

        for(int i = 0; i < events.size(); i++) {
            ConfirmControlEvent event = events.get(i);

            RoundButton btn = new RoundButton(holder.itemView.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, Converter.dpToPx(40)
            );

            if(i == 0) {
                params.setMargins(Converter.dpToPx(16), 0, 0, 0);
            } else if(i == events.size() -1) {
                params.setMargins(Converter.dpToPx(10), 0, Converter.dpToPx(16), 0);
            } else {
                params.setMargins(Converter.dpToPx(10), 0, 0, 0);
            }

            btn.setLayoutParams(params);
            btn.setButtonType(event.getButtonType());
            btn.setText(event.getName());
            holder.buttons.addView(btn);

            RxView.clicks(btn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    data.getDoAfterEvent().run();
                    event.getListener().run();
                });
        }
    }

    @Override
    public void onDelete() {

    }
}