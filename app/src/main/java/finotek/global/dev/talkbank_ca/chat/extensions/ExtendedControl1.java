package finotek.global.dev.talkbank_ca.chat.extensions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.databinding.ChatExControl1Binding;

public class ExtendedControl1 extends Fragment {
    private MessageBox messageBox;
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
                    messageBox.add(new SendMessage("계좌 개설", R.drawable.icon_talkbank01));
                });

        RxView.clicks(binding.transferMoney)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .doOnNext(aVoid -> doOnControl.run())
                .subscribe(aVoid -> messageBox.add(new SendMessage("계좌 이체", R.drawable.icon_talkbank02)));

        RxView.clicks(binding.checkAccount)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .doOnNext(aVoid -> doOnControl.run())
                .subscribe(aVoid -> messageBox.add(new SendMessage("계좌 조회", R.drawable.icon_talkbank03)));

        return rootView;
    }

    public void setMessageBox(MessageBox messageBox, Runnable doOnControl) {
        this.messageBox = messageBox;
        this.doOnControl = doOnControl;
    }
}
