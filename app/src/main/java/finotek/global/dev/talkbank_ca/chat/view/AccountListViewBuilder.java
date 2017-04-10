package finotek.global.dev.talkbank_ca.chat.view;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.Account;
import finotek.global.dev.talkbank_ca.chat.messages.AccountList;
import finotek.global.dev.talkbank_ca.chat.messages.action.EnableToEditMoney;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.databinding.ChatAccountListBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatItemAccountBinding;
import finotek.global.dev.talkbank_ca.util.Converter;

public class AccountListViewBuilder implements ChatView.ViewBuilder<AccountList> {
    private class ViewHolder extends RecyclerView.ViewHolder {
        private ChatAccountListBinding listBinding;
        private List<ChatItemAccountBinding> itemBindings = new ArrayList<>();
        private List<Account> accountList;

        ViewHolder(View itemView) {
            super(itemView);
            setIsRecyclable(false);

            listBinding = DataBindingUtil.bind(itemView);
        }

        public void addAccounts() {
            for(int i = 0; i < accountList.size(); i++) {
                Account act = accountList.get(i);
                ChatItemAccountBinding binding = itemBindings.get(i);

                if(i == accountList.size() - 1) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.main.getLayoutParams();
                    params.setMarginEnd(Converter.dpToPx(16));
                    binding.main.setLayoutParams(params);
                }

                binding.setAccount(act);
                binding.main.setClickable(true);
                RxView.clicks(binding.main)
                    .throttleFirst(200, TimeUnit.MILLISECONDS)
                    .subscribe(aVoid -> {
                        for(int k = 0; k < itemBindings.size(); k++) {
                            ChatItemAccountBinding b = itemBindings.get(k);
                            Account acc = accountList.get(k);
                            if(acc.isFromContact()) {
                                listBinding.accountList.removeView(b.getRoot());
                            } else {
                                b.main.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.border_round_gray));
                            }
                        }

                        binding.main.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.border_round_primary));

                        TransactionDB.INSTANCE.setTxName(act.getName());
                        MessageBox.INSTANCE.add(new EnableToEditMoney());
                    });

                listBinding.accountList.addView(binding.getRoot());
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_account_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, AccountList data) {
        ViewHolder holder = (ViewHolder) viewHolder;
        ViewGroup frame = (ViewGroup) holder.itemView;
        holder.listBinding.accountList.removeAllViews();
        holder.accountList = data.getList();

        if(holder.itemBindings.isEmpty()) {
            for (Account account : data.getList()) {
                final View view = LayoutInflater.from(frame.getContext()).inflate(R.layout.chat_item_account, holder.listBinding.accountList, false);
                ChatItemAccountBinding itemBinding = ChatItemAccountBinding.bind(view);

                holder.itemBindings.add(itemBinding);
            }

            holder.addAccounts();
        }
    }

    @Override
    public void onDelete() {

    }
}