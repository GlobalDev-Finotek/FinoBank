package finotek.global.dev.talkbank_ca.chat;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import finotek.global.dev.talkbank_ca.chat.messages.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.RequestTakeIDCard;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.databinding.ActivityChatBinding;
import finotek.global.dev.talkbank_ca.user.CapturePicFragment;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private MessageBox messageBox;
    private Scenario scenario;

    private boolean isExControlAvailable = false;
    private View exControlView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        setSupportActionBar(binding.toolbar);

        messageBox = new MessageBox();
        scenario = new Scenario(this, binding.chatView, messageBox);
        messageBox.getObservable().subscribe(this::onNewMessageUpdated);

        RxView.focusChanges(binding.footerInputs.chatEditText)
                .delay(100, TimeUnit.MILLISECONDS)
                .subscribe(this::chatEditFieldFocusChanged);

        RxTextView.textChanges(binding.footerInputs.chatEditText)
                .subscribe(this::chatEditFieldTextChanged);

        RxView.clicks(binding.footerInputs.showExControl)
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .delay(100, TimeUnit.MILLISECONDS)
            .subscribe(this::expandControlClickEvent);

        preInitExControlView();
    }

    private void onNewMessageUpdated(Object msg){
        if(msg instanceof RequestTakeIDCard) {
            releaseControls();

            binding.footer.addView(inflate(R.layout.chat_capture));
            CapturePicFragment capturePicFragment = CapturePicFragment.newInstance();
            FragmentTransaction tx = getFragmentManager().beginTransaction();
            tx.add(R.id.chat_capture, capturePicFragment);
            tx.commit();
        }

        if(msg instanceof RequestSignature) {
            releaseControls();
            binding.footer.addView(inflate(R.layout.chat_sign));
        }
    }

    public void onSendButtonClickEvent(View v){
        String msg = binding.footerInputs.chatEditText.getText().toString();
        messageBox.add(new SendMessage(msg));
        clearInput();
    }

    private void expandControlClickEvent(Void aVoid){
        if(isExControlAvailable)
            runOnUiThread(this::hideExControl);
        else
            runOnUiThread(this::showExControl);
    }

    private void chatEditFieldFocusChanged(boolean hasFocus){
        if(hasFocus)
            runOnUiThread(this::hideExControl);
    }

    private void chatEditFieldTextChanged(CharSequence value) {
        binding.footerInputs.sendButton.setEnabled(!value.toString().isEmpty());
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

    private void preInitExControlView(){
        exControlView = inflate(R.layout.chat_extended_control);
    }

    /**
     * 키보드 이외에 화면을 터치한 경우 자동으로 키보드를 dismiss 하기 위한 소스
     * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // dimiss keyboard or hide ex control
            releaseControls();

            View v = getCurrentFocus();
            if(v instanceof EditText) {
                Rect editTextRect = new Rect();
                v.getGlobalVisibleRect(editTextRect);

                Rect sendButtonRect = new Rect();
                binding.footerInputs.sendButton.getGlobalVisibleRect(sendButtonRect);

                if(!editTextRect.contains((int) ev.getRawX(), (int) ev.getRawY())
                        && !sendButtonRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    dismissKeyboard(v);
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private void dismissKeyboard(View v) {
        v.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void releaseControls(){
        dismissKeyboard(binding.footerInputs.chatEditText);
        hideExControl();
    }

    private View inflate(int layoutId){
        ViewGroup parent = (ViewGroup) findViewById(android.R.id.content);
        return LayoutInflater.from(this).inflate(layoutId, parent, false);
    }
}