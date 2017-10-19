package finotek.global.dev.talkbank_ca.chat.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.action.ShowPdfView;
import finotek.global.dev.talkbank_ca.databinding.ChatAccountConfirmBinding;
import finotek.global.dev.talkbank_ca.user.util.AccountImageBuilder;

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