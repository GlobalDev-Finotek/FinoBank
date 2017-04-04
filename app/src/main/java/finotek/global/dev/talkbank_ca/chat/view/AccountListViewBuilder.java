package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.messages.Account;
import finotek.global.dev.talkbank_ca.chat.messages.AccountList;
import finotek.global.dev.talkbank_ca.databinding.ChatAccountListBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatItemAccountBinding;

public class AccountListViewBuilder implements ChatView.ViewBuilder<AccountList> {
    private Account selectedAccount;

    private class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder build(ViewGroup parent) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_account_list, parent, false);
        ChatAccountListBinding binding = ChatAccountListBinding.bind(view);
        return new ViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, AccountList data) {
        ViewHolder holder = (ViewHolder) viewHolder;
        ViewGroup frame = (ViewGroup) holder.itemView;
        ChatAccountListBinding listBinding = DataBindingUtil.getBinding(frame);
        listBinding.accountList.removeAllViews();

        if(selectedAccount != null) {
            addAccount(frame.getContext(), listBinding.accountList, selectedAccount);
        } else {
            for (Account account : data.getList()) {
                addAccount(frame.getContext(), listBinding.accountList, account);
            }
        }
    }

    @Override
    public void onDelete() {
        selectedAccount = null;
    }

    private void addAccount(Context context, ViewGroup list, Account account){
        final View view = LayoutInflater.from(context).inflate(R.layout.chat_item_account, list, false);
        ChatItemAccountBinding itemBinding = ChatItemAccountBinding.bind(view);
        itemBinding.setAccount(account);
        RxView.touches(itemBinding.main)
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .subscribe(e -> {
                if(e.getAction() == MotionEvent.ACTION_UP) {
                    list.removeAllViews();
                    list.addView(view);
                    selectedAccount = account;
                    itemBinding.main.setBackground(ContextCompat.getDrawable(context, R.drawable.border_round_primary));
                }
            });

        list.addView(view);
    }
}