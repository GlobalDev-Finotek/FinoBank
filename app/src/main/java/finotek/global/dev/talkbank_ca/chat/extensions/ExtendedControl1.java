package finotek.global.dev.talkbank_ca.chat.extensions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.databinding.ChatExControl1Binding;

public class ExtendedControl1 extends Fragment {
    private Runnable doOnControl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.chat_ex_control_1, container, false);
        ChatExControl1Binding binding = ChatExControl1Binding.bind(rootView);

        RxView.clicks(binding.registerAccount)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .doOnNext(aVoid -> doOnControl.run())
                .subscribe(aVoid -> {
                    MessageBox.INSTANCE.add(new SendMessage(getContext().getString(R.string.dialog_button_open_account), R.drawable.icon_talkbank01));
                });

        RxView.clicks(binding.transferMoney)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .doOnNext(aVoid -> doOnControl.run())
                .subscribe(aVoid -> MessageBox.INSTANCE.add(new SendMessage(getContext().getString(R.string.dialog_button_transfer), R.drawable.icon_talkbank02)));

        RxView.clicks(binding.checkAccount)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .doOnNext(aVoid -> doOnControl.run())
                .subscribe(aVoid -> MessageBox.INSTANCE.add(new SendMessage(getContext().getString(R.string.main_string_view_account_details), R.drawable.icon_talkbank03)));

        return rootView;
    }

    public void setDoOnControl(Runnable doOnControl) {
        this.doOnControl = doOnControl;
    }
}
