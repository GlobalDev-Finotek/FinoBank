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

/**
 * Created by jungwon on 10/10/2017.
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
    //// TODO: 10/10/2017  
    private class AccountConfirmHolder extends RecyclerView.ViewHolder{
        ChatAccountConfirmBinding binding;

        AccountConfirmHolder(View itemView) {
            super(itemView);
            binding = ChatAccountConfirmBinding.bind(itemView);
            Context context = itemView.getContext();

            String confirm = "11";

            binding.btnConfirmText.setText(String.format("%s.%s", confirm, "pdf"));

            if(Locale.getDefault().getLanguage().equals("ko")) {
                RxView.clicks(binding.btnConfirmPreview)
                        .throttleFirst(200, TimeUnit.MILLISECONDS)
                        .subscribe(aVoid -> {
                            MessageBox.INSTANCE.add(new ShowPdfView(confirm, "loan_service.pdf"));
                        });

                RxView.clicks(binding.btnConfirmSave)
                        .throttleFirst(200, TimeUnit.MILLISECONDS)
                        .subscribe(o -> {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://www.dropbox.com/s/ez3s0lk62pqx1ge/loan_service.pdf?dl=0"));
                            context.startActivity(i);
                        });
            }
            else{
                RxView.clicks(binding.btnConfirmPreview)
                        .throttleFirst(200, TimeUnit.MILLISECONDS)
                        .subscribe(aVoid -> {
                            MessageBox.INSTANCE.add(new ShowPdfView(confirm, "loan_service.pdf"));
                        });

                RxView.clicks(binding.btnConfirmSave)
                        .throttleFirst(200, TimeUnit.MILLISECONDS)
                        .subscribe(o -> {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://www.dropbox.com/s/ez3s0lk62pqx1ge/loan_service.pdf?dl=0"));
                            context.startActivity(i);
                        });

            }
        }
    }

}
