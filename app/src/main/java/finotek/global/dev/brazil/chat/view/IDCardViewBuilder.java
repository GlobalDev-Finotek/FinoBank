package finotek.global.dev.brazil.chat.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.chat.messages.ui.IDCardInfo;
import finotek.global.dev.brazil.databinding.ChatIdCardBinding;

public class IDCardViewBuilder implements ChatView.ViewBuilder<IDCardInfo> {
	private final Context context;

	public IDCardViewBuilder(Context context) {
		this.context = context;
	}

	@Override
	public RecyclerView.ViewHolder build(ViewGroup parent) {
		return new IDCardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_id_card, parent, false));
	}

	@Override
	public void bind(RecyclerView.ViewHolder viewHolder, IDCardInfo data) {
		IDCardViewHolder holder = (IDCardViewHolder) viewHolder;

		holder.binding.idCardInfo.setVisibility(View.VISIBLE);
		holder.binding.name.setText(data.getName());
		holder.binding.birthDate.setText(data.getBirthDate());
	}

	@Override
	public void onDelete() {

	}

	private class IDCardViewHolder extends RecyclerView.ViewHolder {
		ChatIdCardBinding binding;

		public IDCardViewHolder(View itemView) {
			super(itemView);
			binding = DataBindingUtil.bind(itemView);
		}
	}
}
