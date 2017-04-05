package finotek.global.dev.talkbank_ca.chat;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jakewharton.rxbinding.support.v4.view.RxViewPager;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.extensions.ControlPagerAdapter;
import finotek.global.dev.talkbank_ca.chat.messages.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.IDCardInfo;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.Done;
import finotek.global.dev.talkbank_ca.chat.messages.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.RequestTakeIDCard;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransfer;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.databinding.ActivityChatBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatExtendedControlBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatFooterInputBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatTransferBinding;
import finotek.global.dev.talkbank_ca.setting.SettingsActivity;
import finotek.global.dev.talkbank_ca.user.CapturePicFragment;
import finotek.global.dev.talkbank_ca.user.sign.SignRegistFragment;
import finotek.global.dev.talkbank_ca.user.dialogs.SucceededDialog;
import rx.android.schedulers.AndroidSchedulers;

public class ChatActivity extends AppCompatActivity {
	private ActivityChatBinding binding;
	private ChatFooterInputBinding fiBinding;
	private ChatExtendedControlBinding ecBinding;
	private ChatTransferBinding ctBinding;
	private ScenarioChannel scenario;

	private boolean isExControlAvailable = false;
	private View exControlView = null;
	private View footerInputs = null;
	private View transferView = null;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("");
		binding.toolbarTitle.setText("톡뱅");

		scenario = new ScenarioChannel(this, binding.chatView);
		MessageBox.INSTANCE.observable
            .delay(2000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onNewMessageUpdated);
		binding.ibMenu.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, SettingsActivity.class)));

		preInitControlViews();
	}

	private void onNewMessageUpdated(Object msg) {
		if (msg instanceof RequestTakeIDCard) {
			releaseControls();

			binding.footer.addView(inflate(R.layout.chat_capture));
			CapturePicFragment capturePicFragment = CapturePicFragment.newInstance();
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			capturePicFragment.takePicture(path -> {
				MessageBox.INSTANCE.add(new IDCardInfo("주민등록증", "김우섭", "660103-1111111", "2016.3.10"));
				MessageBox.INSTANCE.add(new ReceiveMessage("위 내용이 맞으세요?"));
				MessageBox.INSTANCE.add(new ConfirmRequest());
                this.returnToInitialControl();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.remove(capturePicFragment).commit();
			});
			tx.add(R.id.chat_capture, capturePicFragment);
            tx.commit();
		}

		if (msg instanceof RequestSignature) {
			releaseControls();

            binding.footer.addView(inflate(R.layout.chat_capture));
            SignRegistFragment signRegistFragment = new SignRegistFragment();
            FragmentTransaction tx = getFragmentManager().beginTransaction();
            signRegistFragment.setOnSaveListener(() -> {
                SucceededDialog dialog = new SucceededDialog(ChatActivity.this);
                dialog.setTitle("자필 서명이 인증 되었습니다.");
                dialog.setDescription("확인 버튼을 선택하면\n완료 단계로 이동합니다.");
                dialog.setButtonText("확인");
                dialog.setDoneListener(() -> {
                    MessageBox.INSTANCE.add(new Done());
                    returnToInitialControl();

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.remove(signRegistFragment).commit();

                    dialog.dismiss();
                });
                dialog.show();
            });

            tx.add(R.id.chat_capture, signRegistFragment);
            tx.commit();
		}

		if (msg instanceof RequestTransfer) {
			releaseAllControls();
			binding.footer.addView(transferView);
		}
	}

	public void onSendButtonClickEvent(Void aVoid) {
		String msg = fiBinding.chatEditText.getText().toString();
		MessageBox.INSTANCE.add(new SendMessage(msg));
		clearInput();
	}

	private void expandControlClickEvent(Void aVoid) {
		if (isExControlAvailable)
			runOnUiThread(this::hideExControl);
		else
			runOnUiThread(this::showExControl);
	}

	private void chatEditFieldFocusChanged(boolean hasFocus) {
		if (hasFocus)
			runOnUiThread(this::hideExControl);
	}

	private void chatEditFieldTextChanged(CharSequence value) {
		fiBinding.sendButton.setEnabled(!value.toString().isEmpty());
	}

	private void clearInput() {
		fiBinding.sendButton.setEnabled(false);
		fiBinding.chatEditText.setText("");
	}

	private void hideExControl() {
		isExControlAvailable = false;
		binding.footer.removeView(exControlView);
		fiBinding.showExControl.setImageResource(R.drawable.ic_add_white_24dp);
	}

	private void showExControl() {
		isExControlAvailable = true;
		binding.footer.addView(exControlView);
		fiBinding.showExControl.setImageResource(R.drawable.ic_close_white_24dp);
	}

	private void preInitControlViews() {
		exControlView = inflate(R.layout.chat_extended_control);
		footerInputs = inflate(R.layout.chat_footer_input);
		transferView = inflate(R.layout.chat_transfer);

		fiBinding = ChatFooterInputBinding.bind(footerInputs);
		RxView.focusChanges(fiBinding.chatEditText)
				.delay(100, TimeUnit.MILLISECONDS)
				.subscribe(this::chatEditFieldFocusChanged);

		RxTextView.textChanges(fiBinding.chatEditText)
				.subscribe(this::chatEditFieldTextChanged);

		RxView.clicks(fiBinding.showExControl)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.delay(100, TimeUnit.MILLISECONDS)
				.subscribe(this::expandControlClickEvent);

		RxView.clicks(fiBinding.sendButton)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(this::onSendButtonClickEvent);

		RxTextView.editorActions(fiBinding.chatEditText)
				.subscribe(actionId -> {
					if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND) {
						this.onSendButtonClickEvent(null);
					}
				});

		ecBinding = ChatExtendedControlBinding.bind(exControlView);

        ControlPagerAdapter adapter = new ControlPagerAdapter(getSupportFragmentManager());
        adapter.setDoOnControl(this::hideExControl);
		ecBinding.extendedControl.setAdapter(adapter);
		RxViewPager.pageSelections(ecBinding.extendedControl)
            .subscribe(pos -> {
                if (pos == 0) {
                    ecBinding.bullet1.setBackground(ContextCompat.getDrawable(this, R.drawable.bullet_activated));
                    ecBinding.bullet2.setBackground(ContextCompat.getDrawable(this, R.drawable.bullet_deactivated));
                } else {
                    ecBinding.bullet1.setBackground(ContextCompat.getDrawable(this, R.drawable.bullet_deactivated));
                    ecBinding.bullet2.setBackground(ContextCompat.getDrawable(this, R.drawable.bullet_activated));
                }
            });

		ctBinding = ChatTransferBinding.bind(transferView);
		ctBinding.gvKeypad.addManagableTextField(ctBinding.editMoney);
		ctBinding.gvKeypad.onComplete(() -> {
			ctBinding.editMoney.setText("");
			binding.footer.removeView(transferView);
			binding.footer.addView(footerInputs);

            MessageBox.INSTANCE.add(new Done());
		});

		binding.footer.addView(footerInputs);
	}

	/**
	 * 키보드 이외에 화면을 터치한 경우 자동으로 키보드를 dismiss 하기 위한 소스
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (v instanceof EditText) {
				Rect editTextRect = new Rect();
				v.getGlobalVisibleRect(editTextRect);

				Rect sendButtonRect = new Rect();
				fiBinding.sendButton.getGlobalVisibleRect(sendButtonRect);

				if (!editTextRect.contains((int) ev.getRawX(), (int) ev.getRawY())
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

	private void releaseControls() {
		dismissKeyboard(fiBinding.chatEditText);
		hideExControl();
	}

	private void releaseAllControls() {
		dismissKeyboard(fiBinding.chatEditText);
		binding.footer.removeAllViews();
	}

	private void returnToInitialControl(){
        releaseAllControls();
        binding.footer.addView(footerInputs);
    }

	private View inflate(int layoutId) {
		ViewGroup parent = (ViewGroup) findViewById(android.R.id.content);
		return LayoutInflater.from(this).inflate(layoutId, parent, false);
	}

}