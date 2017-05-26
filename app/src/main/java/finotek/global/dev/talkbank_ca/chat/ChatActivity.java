package finotek.global.dev.talkbank_ca.chat;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding2.support.v4.view.RxViewPager;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

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
import finotek.global.dev.talkbank_ca.chat.messages.action.DismissKeyboard;
import finotek.global.dev.talkbank_ca.chat.messages.action.EnableToEditMoney;
import finotek.global.dev.talkbank_ca.chat.messages.action.RequestKeyboardInput;
import finotek.global.dev.talkbank_ca.chat.messages.action.ShowPdfView;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.contact.RequestSelectContact;
import finotek.global.dev.talkbank_ca.chat.messages.contact.SelectedContact;
import finotek.global.dev.talkbank_ca.chat.messages.control.ConfirmRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.messages.ui.IDCardInfo;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
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
import finotek.global.dev.talkbank_ca.user.dialogs.DangerDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.PdfViewDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.PrimaryDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.SucceededDialog;
import finotek.global.dev.talkbank_ca.user.sign.BaseSignRegisterFragment;
import finotek.global.dev.talkbank_ca.user.sign.OneStepSignRegisterFragment;
import finotek.global.dev.talkbank_ca.util.Converter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

public class ChatActivity extends AppCompatActivity {
	static final int RESULT_PICK_CONTACT = 1;

	@Inject
	DBHelper dbHelper;

	@Inject
	RxEventBus eventBus;

	boolean doubleBackToExitPressedOnce = false;
	private ActivityChatBinding binding;
	private ChatFooterInputBinding fiBinding;
	private ChatExtendedControlBinding ecBinding;
	private ChatTransferBinding ctBinding;
	private boolean isExControlAvailable = false;
	private View exControlView = null;
	private View footerInputs = null;
	private View transferView = null;
	private SecondMainScenario mainScenario;
	private CapturePicFragment capturePicFragment;
	private OneStepSignRegisterFragment signRegistFragment;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
		getComponent().inject(this);


		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("");
		getSupportActionBar().setElevation(0);
		binding.appbar.setOutlineProvider(null);
		binding.toolbarTitle.setText(getString(R.string.main_string_talkbank));
		Intent intent = getIntent();

		LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
		mLayoutManager.setReverseLayout(true);
		mLayoutManager.setStackFromEnd(true);

		binding.chatView.setLayoutManager(mLayoutManager);
        FadeInAnimator animator = new FadeInAnimator(new AccelerateInterpolator(1f));
		binding.chatView.setItemAnimator(animator);

        if (intent != null) {
			boolean isSigned = intent.getBooleanExtra("isSigned", false);
			mainScenario = new SecondMainScenario(this, binding.chatView, eventBus, dbHelper, isSigned);
		}

		MessageBox.INSTANCE.observable
				.flatMap(msg -> {
					if (msg instanceof EnableToEditMoney) {
						return Observable.just(msg)
								.observeOn(AndroidSchedulers.mainThread());
					} else if (msg instanceof MessageEmitted) {
						return Observable.just(msg)
								.debounce(1, TimeUnit.SECONDS)
								.observeOn(AndroidSchedulers.mainThread());
					} else {
						return Observable.just(msg)
								.delay(1, TimeUnit.SECONDS)
								.observeOn(AndroidSchedulers.mainThread());
					}
				})
				.subscribe(this::onNewMessageUpdated, throwable -> {

				}, new Action() {
					@Override
					public void run() throws Exception {
					}
				});
		binding.ibMenu.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, SettingsActivity.class)));

		preInitControlViews();
	}

	@Override
	protected void onPause() {
		super.onPause();
		hideExControl();
	}

	private void onNewMessageUpdated(Object msg) {
		if (msg instanceof RequestTakeIDCard) {
			releaseControls();
			releaseAllControls();


			View captureView = inflate(R.layout.chat_capture);
			binding.footer.addView(captureView);
			capturePicFragment = CapturePicFragment.newInstance();
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			capturePicFragment.takePicture(path -> {
				/*MessageBox.INSTANCE.add(new IDCardInfo("주민등록증", "김우섭", "660103-1111111", "2016.3.10", path));
				MessageBox.INSTANCE.add(new ReceiveMessage(getString(R.string.dialog_chat_correct_information)));
				MessageBox.INSTANCE.add(ConfirmRequest.buildYesOrNo(ChatActivity.this));*/

				this.returnToInitialControl();

				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.remove(capturePicFragment).commit();
			});

			tx.replace(R.id.chat_capture, capturePicFragment);
			tx.commit();
		}

		if (msg instanceof RequestKeyboardInput) {
			openKeyboard(fiBinding.chatEditText);
		}

		if (msg instanceof RequestSignature) {
			releaseControls();
			releaseAllControls();

			View signView = inflate(R.layout.chat_capture);
			binding.footer.addView(signView);
			signRegistFragment = new OneStepSignRegisterFragment();
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			signRegistFragment.setOnSaveListener(() -> {
				PrimaryDialog loadingDialog = new PrimaryDialog(ChatActivity.this);
				loadingDialog.setTitle(getString(R.string.registration_string_signature_verifying));
				loadingDialog.setDescription(getString(R.string.registration_string_wait));
				loadingDialog.show();

				Observable.interval(1, TimeUnit.SECONDS)
						.observeOn(AndroidSchedulers.mainThread())
						.first((long) 1)
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

								returnToInitialControl();
							});
							dialog.show();
						}, throwable -> {
						});
			});

			signRegistFragment.setOnSizeControlClick(new BaseSignRegisterFragment.OnSizeControlClick() {

				boolean isFullSize = false;

				@Override
				public void onClick(BaseSignRegisterFragment.CanvasSize size) {

					if (!isFullSize) {
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
						signView.setLayoutParams(lp);
					} else {
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT, Converter.dpToPx(350));
						signView.setLayoutParams(lp);
					}

					isFullSize = !isFullSize;
				}
			});

			tx.replace(R.id.chat_capture, signRegistFragment);
			tx.commit();
		}

		if (msg instanceof RequestTransferUI) {
			releaseAllControls();

			int balance = TransactionDB.INSTANCE.getBalance();
			ctBinding.balance.setText(NumberFormat.getNumberInstance().format(balance));
			ctBinding.editMoney.setEnabled(false);
			binding.footer.addView(ctBinding.getRoot());
		}

		if (msg instanceof EnableToEditMoney) {
			ctBinding.editMoney.setEnabled(true);
			ctBinding.editMoney.requestFocus();
			ctBinding.gvKeypad.setLengthLimit(7);
		}

		if (msg instanceof ShowPdfView) {
			ShowPdfView action = (ShowPdfView) msg;
			PdfViewDialog dialog = new PdfViewDialog(this);
			dialog.setTitle(action.getTitle());
			dialog.setPdfAssets(action.getPdfAsset());
			dialog.show();
		}

		if (msg instanceof RequestRemoveControls) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			if (capturePicFragment != null) {
				transaction.remove(capturePicFragment);
			}
			if (signRegistFragment != null) {
				transaction.remove(signRegistFragment);
			}
			transaction.commit();

			this.returnToInitialControl();
		}

		if (msg instanceof RequestSelectContact) {
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, RESULT_PICK_CONTACT);
		}

		if (msg instanceof DismissKeyboard) {
			returnToInitialControl();
		}
	}

	public void onSendButtonClickEvent() {
		String msg = fiBinding.chatEditText.getText().toString();
		MessageBox.INSTANCE.add(new SendMessage(msg));
		clearInput();
	}

	private void expandControlClickEvent() {
		if (isExControlAvailable)
			runOnUiThread(this::hideExControl);
		else
			runOnUiThread(this::showExControl);
	}

	private void chatEditFieldFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			runOnUiThread(this::hideExControl);
		}
	}

	private void chatEditFieldTextChanged(CharSequence value) {
        boolean enabled = !value.toString().isEmpty();
        fiBinding.sendButton.setEnabled(enabled);

        if(enabled) {
            fiBinding.sendButton.setImageResource(R.drawable.btn_send);
        } else {
            fiBinding.sendButton.setImageResource(R.drawable.btn_mike);
        }
	}

	private void clearInput() {
		fiBinding.sendButton.setEnabled(false);
		fiBinding.chatEditText.setText("");
	}

	private void hideExControl() {
		isExControlAvailable = false;
		binding.footer.removeView(exControlView);
	}

	private void showExControl() {
		isExControlAvailable = true;
		binding.footer.addView(exControlView, 0);
	}

	private void preInitControlViews() {
		Log.d("FINOTEK", "preInitControlViews");

		footerInputs = inflate(R.layout.chat_footer_input);
		transferView = inflate(R.layout.chat_transfer);

		fiBinding = ChatFooterInputBinding.bind(footerInputs);
        fiBinding.sendButton.setEnabled(false);

		RxView.focusChanges(fiBinding.chatEditText)
				.delay(100, TimeUnit.MILLISECONDS)
				.subscribe(this::chatEditFieldFocusChanged);

		RxTextView.textChanges(fiBinding.chatEditText)
				.subscribe(this::chatEditFieldTextChanged);

		RxView.clicks(fiBinding.showExControl)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.delay(100, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					expandControlClickEvent();
				});

		RxView.clicks(fiBinding.sendButton)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					onSendButtonClickEvent();
				});

		RxTextView.editorActions(fiBinding.chatEditText)
				.subscribe(actionId -> {
					if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND) {
						this.onSendButtonClickEvent();
					}
				});

		exControlView = inflate(R.layout.chat_extended_control);
		ecBinding = DataBindingUtil.bind(exControlView);

		ctBinding = ChatTransferBinding.bind(transferView);
		ctBinding.gvKeypad.addManagableTextField(ctBinding.editMoney);
		ctBinding.gvKeypad.onComplete(() -> {
			// 잔액
			int balance = TransactionDB.INSTANCE.getBalance();
			String moneyAsString = ctBinding.editMoney.getText().toString();
			int money = 0;
			try {
				money = Integer.valueOf(moneyAsString.replaceAll(",", ""));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			if (money > balance) {
				DangerDialog dialog = new DangerDialog(this);
				dialog.setTitle(getString(R.string.common_string_warning));
				dialog.setDescription(getString(R.string.dialog_string_lack_of_balance));
				dialog.setButtonText(getString(R.string.setting_string_yes));
				dialog.setDoneListener(() -> {
					ctBinding.editMoney.setText("");
					ctBinding.editMoney.requestFocus();

					dialog.dismiss();
				});
				dialog.show();
			} else {
				TransactionDB.INSTANCE.setTxMoney(moneyAsString);

				ctBinding.editMoney.setText("");
				this.returnToInitialControl();

				MessageBox.INSTANCE.add(new TransferButtonPressed());
			}
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

	private void openKeyboard(View v) {
		v.requestFocus();
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(v, 0);
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

		switch (requestCode) {
			case RESULT_PICK_CONTACT:
				if (data != null) {
					Uri contactData = data.getData();
					Cursor c = getContentResolver().query(contactData, null, null, null, null);
					if (c.moveToFirst()) {
						String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
						String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
						String cNumber = "";

						if (hasPhone.equalsIgnoreCase("1")) {
							Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
							phones.moveToFirst();
							cNumber = phones.getString(phones.getColumnIndex("data1"));
						}

						MessageBox.INSTANCE.add(new SelectedContact(name, cNumber));
					}
				}
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mainScenario.release();
		eventBus.clear();
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, getString(R.string.main_back_exit), Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
	}

	private ChatComponent getComponent() {
		return DaggerChatComponent
				.builder()
				.appComponent(((MyApplication) getApplication()).getMyAppComponent())
				.activityModule(new ActivityModule(this))
				.build();
	}

}