package finotek.global.dev.brazil.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.messages.StatusMessage;

public class StatusViewBuilder implements ChatView.ViewBuilder<StatusMessage> {
	@Override
	public RecyclerView.ViewHolder build(ViewGroup parent) {
		RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_status, parent, false);
		return new StatusViewBuilder.StatusViewHolder(view);
	}

	@Override
	public void bind(RecyclerView.ViewHolder viewHolder, StatusMessage data) {
		StatusViewBuilder.StatusViewHolder holder = (StatusViewBuilder.StatusViewHolder) viewHolder;
		holder.message.setText(data.getMessage());
	}

	@Override
	public void onDelete() {

	}

	private class StatusViewHolder extends RecyclerView.ViewHolder {
		TextView message;

		StatusViewHolder(View itemView) {
			super(itemView);
			this.message = (TextView) itemView.findViewById(R.id.message);
		}
	}
}
