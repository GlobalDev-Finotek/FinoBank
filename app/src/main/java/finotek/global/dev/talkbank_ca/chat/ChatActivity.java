package finotek.global.dev.talkbank_ca.chat;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {
    private boolean isExControlAvailable = false;
    private View exControlView = null;
    private Scenario scenario;
    private Handler handler;

    private ActivityChatBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(getMainLooper());

        ViewGroup parent = (ViewGroup) findViewById(android.R.id.content);
        exControlView = LayoutInflater.from(this).inflate(R.layout.chat_extended_control, parent, false);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        binding.chatView.setLayoutManager(manager);
        scenario = new Scenario(binding.chatView);

        RxView.focusChanges(binding.footerInputs.chatEditText)
                .delay(100, TimeUnit.MILLISECONDS)
                .subscribe(hasFocus -> {
                    if(hasFocus)
                        handler.post(this::hideExControl);
                });

        RxTextView.textChanges(binding.footerInputs.chatEditText)
                .subscribe(value -> {
                    binding.footerInputs.sendButton.setEnabled(!value.toString().isEmpty());
                });

        RxView.clicks(binding.footerInputs.showExControl)
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .delay(100, TimeUnit.MILLISECONDS)
            .subscribe(aVoid -> {
                if(isExControlAvailable)
                    handler.post(this::hideExControl);
                else
                    handler.post(this::showExControl);
            });

        RxView.clicks(binding.footerInputs.sendButton)
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .subscribe(aVoid -> {
                String msg = binding.footerInputs.chatEditText.getText().toString();
                scenario.sendMessage(msg);
                clearInput();
            });
    }

    /**
    * 키보드 이외에 화면을 터치한 경우 자동으로 키보드를 dismiss 하기 위한 소스
    * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if(v instanceof EditText) {
                Rect editTextRect = new Rect();
                v.getGlobalVisibleRect(editTextRect);

                Rect sendButtonRect = new Rect();
                binding.footerInputs.sendButton.getGlobalVisibleRect(sendButtonRect);

                if(!editTextRect.contains((int) ev.getRawX(), (int) ev.getRawY())
                        && !sendButtonRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private void clearInput() {
        binding.footerInputs.sendButton.setEnabled(false);
        binding.footerInputs.chatEditText.setText("");
    }

    private void hideExControl() {
        isExControlAvailable = false;
        binding.footer.removeView(exControlView);
        binding.footerInputs.showExControl.setImageResource(R.drawable.ic_add_white_24dp);
    }

    private void showExControl() {
        isExControlAvailable = true;
        binding.footer.addView(exControlView);
        binding.footerInputs.showExControl.setImageResource(R.drawable.ic_close_white_24dp);
    }
}