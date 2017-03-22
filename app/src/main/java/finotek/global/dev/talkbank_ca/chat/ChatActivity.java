package finotek.global.dev.talkbank_ca.chat;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.view.ExtendedControlBuilder;
import finotek.global.dev.talkbank_ca.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {
    private boolean isExControlAvailable = false;
    private View exControlView = null;
    private Channel channel;
    private Handler handler;

    private ActivityChatBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        handler = new Handler(getMainLooper());

        ViewGroup parent = (ViewGroup) findViewById(android.R.id.content);
        exControlView = ExtendedControlBuilder.build(this, parent);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        binding.chatView.setLayoutManager(manager);

        channel = new Channel(binding.chatView);
        channel.receiveMessage("홍길동님 안녕하세요. 무엇을 도와드릴까요?");

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
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if(v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(ev);
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