package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;

public class IconicSendViewBuilder implements ChatView.ViewBuilder<SendMessage> {
	private Context context;

	public IconicSendViewBuilder(Context context) {
		this.context = context;
	}

	@Override
	public RecyclerView.ViewHolder build(ViewGroup parent) {
		RelativeLayout view = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_iconic_send, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void bind(RecyclerView.ViewHolder viewHolder, SendMessage data) {
		ViewHolder holder = (ViewHolder) viewHolder;
		holder.message.setText(data.getMessage());
		holder.icon.setImageDrawable(ContextCompat.getDrawable(context, data.getIcon()));
	}

	@Override
	public void onDelete() {

	}

	private class ViewHolder extends RecyclerView.ViewHolder {
		TextView message;
		ImageView icon;

		ViewHolder(View itemView) {
			super(itemView);
			this.message = (TextView) itemView.findViewById(R.id.message);
			this.icon = (ImageView) itemView.findViewById(R.id.icon);
		}
	}
}
