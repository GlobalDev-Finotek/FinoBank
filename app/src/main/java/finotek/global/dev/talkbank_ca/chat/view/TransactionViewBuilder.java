package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MainScenario;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.ApplyScenario;
import finotek.global.dev.talkbank_ca.chat.messages.RecentTransaction;
import finotek.global.dev.talkbank_ca.chat.messages.Transaction;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferToSomeone;
import finotek.global.dev.talkbank_ca.databinding.ChatItemTransactionBinding;

public class TransactionViewBuilder implements ChatView.ViewBuilder<RecentTransaction> {
    private Context context;

    public TransactionViewBuilder(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.chat_transaction_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, RecentTransaction data) {
        ViewHolder holder = (ViewHolder) viewHolder;
        LinearLayout group = (LinearLayout) holder.itemView;
        group.removeAllViews();

        for (Transaction tx : data.getTransactions()) {
            Context context = group.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_transaction, group, false);
            ChatItemTransactionBinding binding = ChatItemTransactionBinding.bind(view);
            binding.setItem(tx);
            group.addView(view);

            RxView.clicks(binding.transferBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    MessageBox.INSTANCE.add(new ApplyScenario("transfer"));
	                MessageBox.INSTANCE.add(new TransferToSomeone(tx.getName(), tx.getPrice()));
                });
        }
    }

    @Override
    public void onDelete() {

    }

	class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View itemView) {
			super(itemView);
		}
	}
}
