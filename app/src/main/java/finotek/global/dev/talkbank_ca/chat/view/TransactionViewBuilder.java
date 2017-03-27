package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.RecentTransaction;
import finotek.global.dev.talkbank_ca.chat.messages.Transaction;
import finotek.global.dev.talkbank_ca.databinding.ChatItemTransactionBinding;

public class TransactionViewBuilder implements ChatView.ViewBuilder<RecentTransaction> {
    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
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

        for (Transaction tx : data.getTransactions()) {
            ChatItemTransactionBinding binding = ChatItemTransactionBinding.inflate(LayoutInflater.from(group.getContext()));
            binding.setItem(tx);
            group.addView(binding.main);
        }
    }
}
