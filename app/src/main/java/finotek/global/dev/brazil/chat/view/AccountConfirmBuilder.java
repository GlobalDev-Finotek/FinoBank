package finotek.global.dev.brazil.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import finotek.global.dev.brazil.R;
import finotek.global.dev.brazil.databinding.ChatAccountConfirmBinding;
import finotek.global.dev.brazil.user.util.AccountImageBuilder;

/**
 * Created by jungwon on 10/11/2017.
 */

public class AccountConfirmBuilder implements ChatView.ViewBuilder<Void> {
	@Override
	public RecyclerView.ViewHolder build(ViewGroup parent) {
		return new AccountConfirmHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_account_confirm, parent, false));
	}

	@Override
	public void bind(RecyclerView.ViewHolder viewHolder, Void data) {

	}

	@Override
	public void onDelete() {

	}

	private class AccountConfirmHolder extends RecyclerView.ViewHolder {
		ChatAccountConfirmBinding binding;

		AccountConfirmHolder(View itemView) {
			super(itemView);
			binding = ChatAccountConfirmBinding.bind(itemView);
			binding.accountImage.setImageBitmap(AccountImageBuilder.getAccount(itemView.getContext()));
		}
	}
}