package finotek.global.dev.talkbank_ca.chat;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.Toast;

import com.jakewharton.rxbinding.support.v4.view.RxViewPager;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.chat.extensions.ControlPagerAdapter;
import finotek.global.dev.talkbank_ca.chat.messages.MessageEmitted;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.WaitForMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.DismissKeyboard;
import finotek.global.dev.talkbank_ca.chat.messages.action.EnableToEditMoney;
import finotek.global.dev.talkbank_ca.chat.messages.action.ShowPdfView;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.contact.RequestSelectContact;
import finotek.global.dev.talkbank_ca.chat.messages.contact.SelectedContact;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransfer;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.messages.ui.IDCardInfo;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestTakeIDCard;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.databinding.ActivityChatBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatExtendedControlBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatFooterInputBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatTransferBinding;
import finotek.global.dev.talkbank_ca.inject.component.ChatComponent;
import finotek.global.dev.talkbank_ca.inject.component.DaggerChatComponent;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.model.DBHelper;
import finotek.global.dev.talkbank_ca.setting.SettingsActivity;
import finotek.global.dev.talkbank_ca.user.CapturePicFragment;
import finotek.global.dev.talkbank_ca.user.dialogs.PdfViewDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.PrimaryDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.SucceededDialog;
import finotek.global.dev.talkbank_ca.user.sign.OneStepSignRegisterFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static android.widget.Toast.LENGTH_LONG;

public class ChatActivity extends AppCompatActivity {
	@Inject
	RxEventBus eventBus;

	@Inject
	DBHelper userDBHelper;

	private ActivityChatBinding binding;
	private ChatFooterInputBinding fiBinding;
	private ChatExtendedControlBinding ecBinding;
	private ChatTransferBinding ctBinding;
	private boolean isExControlAvailable = false;
	private View exControlView = null;
	private View footerInputs = null;
	private View transferView = null;

    static final int RESULT_PICK_CONTACT = 1;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

		getComponent().inject(this);

		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("");
		binding.toolbarTitle.setText(getString(R.string.main_string_talkbank));

		ScenarioChannel.INSTANCE.init(this, binding.chatView, eventBus, userDBHelper);

		MessageBox.INSTANCE.observable
            .flatMap(msg -> {
                if(msg instanceof EnableToEditMoney) {
                    return Observable.just(msg)
                        .observeOn(AndroidSchedulers.mainThread());
                } else if(msg instanceof MessageEmitted || msg instanceof WaitForMessage) {
                    return Observable.just(msg)
                        .debounce(2, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread());
                } else {
                    return Observable.just(msg)
                        .delay(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread());
                }
            })
            .subscribe(this::onNewMessageUpdated);
		binding.ibMenu.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, SettingsActivity.class)));

		preInitControlViews();
	}

	private void onNewMessageUpdated(Object msg) {
        if(msg instanceof WaitForMessage) {
            binding.waitMessage.setVisibility(View.VISIBLE);
        }

        if(msg instanceof MessageEmitted) {
            binding.waitMessage.setVisibility(View.INVISIBLE);
        }

		if (msg instanceof RequestTakeIDCard) {
			releaseControls();

			binding.footer.addView(inflate(R.layout.chat_capture));
			CapturePicFragment capturePicFragment = CapturePicFragment.newInstance();
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			capturePicFragment.takePicture(path -> {
				MessageBox.INSTANCE.add(new IDCardInfo("주민등록증", "김우섭", "660103-1111111", "2016.3.10"));
				MessageBox.INSTANCE.add(new ReceiveMessage(getString(R.string.dialog_chat_correct_information)));
				MessageBox.INSTANCE.add(ConfirmRequest.buildYesOrNo(ChatActivity.this));
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
			OneStepSignRegisterFragment signRegistFragment = new OneStepSignRegisterFragment();
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			signRegistFragment.setOnSaveListener(() -> {
                PrimaryDialog loadingDialog = new PrimaryDialog(ChatActivity.this);
                loadingDialog.setTitle(getString(R.string.registration_string_signature_verifying));
                loadingDialog.setDescription(getString(R.string.registration_string_wait));
                loadingDialog.show();

                Observable.interval(1500, TimeUnit.MILLISECONDS).first()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(i -> {
                        loadingDialog.dismiss();

                        SucceededDialog dialog = new SucceededDialog(ChatActivity.this);
                        dialog.setTitle(getString(R.string.setting_string_signature_verified));
                        dialog.setDescription(getString(R.string.setting_string_authentication_complete));
                        dialog.setButtonText(getString(R.string.setting_string_yes));
                        dialog.setDoneListener(() -> {
                            MessageBox.INSTANCE.add(new SignatureVerified());
                            returnToInitialControl();

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.remove(signRegistFragment).commit();

                            dialog.dismiss();
                        });
                        dialog.show();
                    });
			});

			tx.add(R.id.chat_capture, signRegistFragment);
			tx.commit();
		}

		if (msg instanceof RequestTransfer) {
			releaseAllControls();

			int balance = TransactionDB.INSTANCE.getBalance();
			ctBinding.balance.setText(NumberFormat.getNumberInstance().format(balance));
            ctBinding.editMoney.setEnabled(false);
			binding.footer.addView(ctBinding.getRoot());
		}

		if(msg instanceof EnableToEditMoney) {
            ctBinding.editMoney.setEnabled(true);
            ctBinding.editMoney.requestFocus();
        }

		if(msg instanceof ShowPdfView) {
            ShowPdfView action = (ShowPdfView) msg;
            PdfViewDialog dialog = new PdfViewDialog(this);
            dialog.setTitle(action.getTitle());
            dialog.setPdfAssets(action.getPdfAsset());
            dialog.show();
        }

        if(msg instanceof RequestSelectContact) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, RESULT_PICK_CONTACT);
		}

		if (msg instanceof DismissKeyboard) {
			returnToInitialControl();
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
        adapter.setSettingControl(() -> startActivity(new Intent(ChatActivity.this, SettingsActivity.class)));
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
            String moneyAsString = ctBinding.editMoney.getText().toString();
			int money = Integer.valueOf(moneyAsString.replaceAll(",", ""));
			TransactionDB.INSTANCE.transferMoney(money);
            TransactionDB.INSTANCE.setTxMoney(moneyAsString);

			ctBinding.editMoney.setText("");
			this.returnToInitialControl();

			MessageBox.INSTANCE.add(new TransferButtonPressed());
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

	private void returnToInitialControl() {
		releaseAllControls();
		binding.footer.addView(footerInputs);
	}

	private View inflate(int layoutId) {
		ViewGroup parent = (ViewGroup) findViewById(android.R.id.content);
		return LayoutInflater.from(this).inflate(layoutId, parent, false);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case RESULT_PICK_CONTACT:
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if(c.moveToFirst()) {
                    String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String cNumber = "";

                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                null, null);
                        phones.moveToFirst();
                        cNumber = phones.getString(phones.getColumnIndex("data1"));
                    }

                    MessageBox.INSTANCE.add(new SelectedContact(name, cNumber));
                }
                break;
        }
    }

    private ChatComponent getComponent() {
		return DaggerChatComponent
				.builder()
				.appComponent(((MyApplication) getApplication()).getAppComponent())
				.activityModule(new ActivityModule(this))
				.build();
	}

}