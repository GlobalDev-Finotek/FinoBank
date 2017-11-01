package finotek.global.dev.brazil.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.messages.SendMessage;

public class SendViewBuilder implements ChatView.ViewBuilder<SendMessage> {
	@Override
	public RecyclerView.ViewHolder build(ViewGroup parent) {
		RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_send, parent, false);
		view.setTag(SendViewHolder.class.getName());
		return new SendViewHolder(view);
	}

	@Override
	public void bind(RecyclerView.ViewHolder viewHolder, SendMessage data) {
		SendViewHolder holder = (SendViewHolder) viewHolder;
		holder.message.setText(data.getMessage());
	}

	@Override
	public void onDelete() {

	}

	public class SendViewHolder extends RecyclerView.ViewHolder {
		TextView message;

		SendViewHolder(View itemView) {
			super(itemView);
			this.message = (TextView) itemView.findViewById(R.id.message);
		}
	}
}
