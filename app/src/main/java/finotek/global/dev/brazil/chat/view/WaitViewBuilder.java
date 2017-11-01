package finotek.global.dev.brazil.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import finotek.global.dev.brazil.R;

public class WaitViewBuilder implements ChatView.ViewBuilder<Void> {
	@Override
	public RecyclerView.ViewHolder build(ViewGroup parent) {
		RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_wait, parent, false);
		return new WaitViewHolder(view);
	}

	@Override
	public void bind(RecyclerView.ViewHolder viewHolder, Void data) {
		// nothing to do
	}

	@Override
	public void onDelete() {

	}

	private class WaitViewHolder extends RecyclerView.ViewHolder {
		WaitViewHolder(View itemView) {
			super(itemView);
		}
	}
}