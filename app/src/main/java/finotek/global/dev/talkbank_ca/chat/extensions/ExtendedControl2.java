package finotek.global.dev.talkbank_ca.chat.extensions;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.chat.MessageBox;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.databinding.ChatExControl2Binding;
import finotek.global.dev.talkbank_ca.setting.SettingsActivity;

public class ExtendedControl2 extends Fragment {
    private Runnable doOnControl;
    private Runnable settingControl;

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
                MessageBox.INSTANCE.add(new SendMessage(getContext().getString(R.string.main_string_secured_mirocredit), R.drawable.icon_talkbank04));
            });

        RxView.clicks(binding.btnSetting)
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .doOnNext(aVoid -> doOnControl.run())
            .subscribe(aVoid -> {
                settingControl.run();
            });

        RxView.clicks(binding.btnSendEmail)
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .doOnNext(aVoid -> doOnControl.run())
            .subscribe(aVoid -> {
                MessageBox.INSTANCE.add(new SendMessage(getContext().getString(R.string.main_button_send_the_conversation_to_e_mail), R.drawable.icon_talkbank06));
            });
        
        return rootView;
    }

    public void setDoOnControl(Runnable doOnControl) {
        this.doOnControl = doOnControl;
    }

    public void setSettingControl(Runnable settingControl) {
        this.settingControl = settingControl;
    }
}
