package finotek.global.dev.brazil.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.messages.control.ConfirmControlEvent;
import finotek.global.dev.brazil.chat.messages.control.ConfirmRequest;
import finotek.global.dev.brazil.util.Converter;
import finotek.global.dev.brazil.widget.RoundButton;
import finotek.global.dev.brazil.widget.TalkBankButton;

public class ConfirmViewBuilder implements ChatView.ViewBuilder<ConfirmRequest> {
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

		for (int i = 0; i < events.size(); i++) {
			ConfirmControlEvent event = events.get(i);

			RoundButton btn = new RoundButton(holder.itemView.getContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, Converter.dpToPx(40)
			);

			if (i == 0) {
				params.setMargins(Converter.dpToPx(16), 0, 0, 0);
			} else if (i == events.size() - 1) {
				params.setMargins(Converter.dpToPx(10), 0, Converter.dpToPx(16), 0);
			} else {
				params.setMargins(Converter.dpToPx(10), 0, 0, 0);
			}

			int padding = Converter.dpToPx(15);
			btn.setPadding(padding, 0, padding, 0);
			btn.setLayoutParams(params);
			btn.setButtonType(event.getButtonType());
			btn.setText(event.getName());
			btn.setFontType(TalkBankButton.FontType.Medium);

			holder.buttons.addView(btn);
			btn.requestLayout();

			RxView.clicks(btn)
					.throttleFirst(2000, TimeUnit.MILLISECONDS)
					.subscribe(aVoid -> {
						if (event.isDisappearAfter()) {
							data.getDoAfterEvent().run();

						}
						event.getListener().run();
					});

			RxView.layoutChanges(btn)
					.subscribe(param -> {
						btn.setPadding(padding, 0, padding, 0);
						btn.requestLayout();
					});
		}
	}

	@Override
	public void onDelete() {

	}

	class ConfirmViewHolder extends RecyclerView.ViewHolder {
		LinearLayout buttons;

		public ConfirmViewHolder(View itemView) {
			super(itemView);
			buttons = (LinearLayout) itemView.findViewById(R.id.buttons);
		}
	}
}