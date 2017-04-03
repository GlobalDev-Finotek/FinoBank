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
import finotek.global.dev.talkbank_ca.databinding.ChatExControl2Binding;

public class ExtendedControl2 extends Fragment {
    private MessageBox messageBox;
    private Runnable doOnControl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.chat_ex_control_2, container, false);
        ChatExControl2Binding binding = ChatExControl2Binding.bind(rootView);

        RxView.clicks(binding.btnLoan)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .doOnNext(aVoid -> doOnControl.run())
                .subscribe(aVoid -> {
                    messageBox.add(new SendMessage("소액 담보 대출", R.drawable.icon_talkbank04));
                });

        RxView.clicks(binding.btnSetting)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .doOnNext(aVoid -> doOnControl.run())
                .subscribe(aVoid -> messageBox.add(new SendMessage("환경설정", R.drawable.icon_talkbank05)));

        RxView.clicks(binding.btnSendEmail)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .doOnNext(aVoid -> doOnControl.run())
                .subscribe(aVoid -> messageBox.add(new SendMessage("상담 내용 이메일로 보내기", R.drawable.icon_talkbank06)));

        return rootView;
    }

    public void setMessageBox(MessageBox messageBox, Runnable doOnControl) {
        this.messageBox = messageBox;
        this.doOnControl = doOnControl;
    }
}
