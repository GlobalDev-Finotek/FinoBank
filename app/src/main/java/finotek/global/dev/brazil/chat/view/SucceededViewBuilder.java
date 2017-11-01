package finotek.global.dev.brazil.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.messages.SucceededMessage;

public class SucceededViewBuilder implements ChatView.ViewBuilder<SucceededMessage> {
	@Override
	public RecyclerView.ViewHolder build(ViewGroup parent) {
		RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_succeeded, parent, false);
		return new SucceededViewBuilder.SucceededViewHolder(view);
	}

	@Override
	public void bind(RecyclerView.ViewHolder viewHolder, SucceededMessage data) {
		SucceededViewBuilder.SucceededViewHolder holder = (SucceededViewBuilder.SucceededViewHolder) viewHolder;
		holder.message.setText(data.getMessage());
	}

	@Override
	public void onDelete() {

	}

	private class SucceededViewHolder extends RecyclerView.ViewHolder {
		TextView message;

		SucceededViewHolder(View itemView) {
			super(itemView);
			this.message = (TextView) itemView.findViewById(R.id.message);
		}
	}
}
