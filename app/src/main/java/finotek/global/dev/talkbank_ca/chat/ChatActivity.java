package finotek.global.dev.talkbank_ca.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.finotek.finocr.common.CryptoUtil;
import com.finotek.finocr.listener.OcrResultLinstener;
import com.finotek.finocr.manager.LibraryInterface;
import com.finotek.finocr.vo.OcrParam;
import com.finotek.finocr.vo.OcrResult;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.chat.messages.MessageEmitted;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RequestContactPermission;
import finotek.global.dev.talkbank_ca.chat.messages.RequestUserInformation;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.DismissKeyboard;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.EnableToEditMoney;
import finotek.global.dev.talkbank_ca.chat.messages.action.RequestKeyboardInput;
import finotek.global.dev.talkbank_ca.chat.messages.action.ShowPdfView;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.contact.RequestSelectContact;
import finotek.global.dev.talkbank_ca.chat.messages.contact.SelectedContact;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.cpi.CPIAuthenticationDone;
import finotek.global.dev.talkbank_ca.chat.messages.cpi.CPIAuthenticationWait;
import finotek.global.dev.talkbank_ca.chat.messages.cpi.RemoteCallDone;
import finotek.global.dev.talkbank_ca.chat.messages.cpi.RequestRemoteCall;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI;
import finotek.global.dev.talkbank_ca.chat.messages.ui.IDCardInfo;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestTakeIDCard;
import finotek.global.dev.talkbank_ca.databinding.ActivityChatBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatFooterInputBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatTransferBinding;
import finotek.global.dev.talkbank_ca.inject.component.ChatComponent;
import finotek.global.dev.talkbank_ca.inject.component.DaggerChatComponent;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.setting.SettingsActivity;
import finotek.global.dev.talkbank_ca.user.CapturePicFragment;
import finotek.global.dev.talkbank_ca.user.cardif.ContractFragment;
import finotek.global.dev.talkbank_ca.user.cardif.RemoteCallFragment;
import finotek.global.dev.talkbank_ca.user.dialogs.ContractPdfViewDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.PrimaryDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.SucceededDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.WarningDialog;
import finotek.global.dev.talkbank_ca.util.Converter;
import finotek.global.dev.talkbank_ca.util.KeyboardUtils;
import globaldev.finotek.com.finosign.view.SignValidationFragment;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

public class ChatActivity extends AppCompatActivity {
	static final int RESULT_PICK_CONTACT = 1;
	private static final int PERMISSION_CAMERA = 2;


	boolean doubleBackToExitPressedOnce = false;
	private ActivityChatBinding binding;
	private ChatFooterInputBinding fiBinding;
	private ChatTransferBinding ctBinding;
	private boolean isExControlAvailable = false;
	private View exControlView = null;
	private View footerInputs = null;
	private View transferView = null;

	private CardifScenario mainScenario;

	private CapturePicFragment capturePicFragment;
	private SignValidationFragment signValidationFragment;
	private RemoteCallFragment remoteCallFragment;
	private ContractFragment contractFragment;

	private OcrResultLinstener ocrResultListener = new OcrResultLinstener() {
		@Override
		public void onOcrSuccess(OcrResult ocrResult) {
			LibraryInterface.setOcrResult(ocrResult);
			MessageBox.INSTANCE.addAndWait(getIdCardInfo(ocrResult));
			ocrResult.clear();
		}

		@Override
		public void onOcrFail() {
			MessageBox.INSTANCE.addAndWait(new Done(),
					new ReceiveMessage(getResources().getString(R.string.dialog_chat_ocr_failed)),
					new RecommendScenarioMenuRequest(getApplicationContext()));
		}
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
		// getComponent().inject(this);


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
		// TODO 아이템 간 패딩 정리
		// binding.chatView.addItemDecoration(new ViewItemD3ecoration());

		if (intent != null) {
			boolean isSigned = intent.getBooleanExtra("isSigned", false);

			mainScenario = new CardifScenario(this, binding.chatView, isSigned);
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

		// keyboard event
		KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
			@Override
			public void onToggleSoftKeyboard(boolean isVisible) {
				if (isVisible)
					binding.chatView.scrollToBottom();
			}
		});

		LibraryInterface.registerOcrResultLinstener(ocrResultListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void onNewMessageUpdated(Object msg) {
		if (msg instanceof RequestTakeIDCard) {
			startOcrModule();
		}

		if (msg instanceof RequestRemoteCall) {
			releaseControls();
			releaseAllControls();
			binding.footer.setPadding(0, 0, 0, 0);
			hideAppBar();
			hideStatusBar();

			View captureView = inflate(R.layout.chat_capture);
			binding.footer.addView(captureView);

			remoteCallFragment = new RemoteCallFragment();
			remoteCallFragment.setVideoURL("android.resource://" + getPackageName() + "/" + R.raw.lady_teller);
			remoteCallFragment.setStopListener(() -> {
				MessageBox.INSTANCE.add(new RemoteCallDone(), 500);
			});

			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.chat_capture, remoteCallFragment);
			tx.commit();
		}

		if (msg instanceof RemoteCallDone) {
			showAppBar();
			this.returnToInitialControl();
			showStatusBar();

			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.remove(remoteCallFragment).commit();
			binding.chatView.scrollToBottom();
		}

		if (msg instanceof CPIAuthenticationWait) {
			PrimaryDialog loadingDialog = new PrimaryDialog(ChatActivity.this);
			loadingDialog.setTitle("We are searching your information.");
			loadingDialog.setDescription("Please wait.");
			loadingDialog.show();

			Observable.interval(4, TimeUnit.SECONDS)
					.observeOn(AndroidSchedulers.mainThread())
					.first((long) 1)
					.subscribe(i -> {
						MessageBox.INSTANCE.add(new CPIAuthenticationDone());
						loadingDialog.dismiss();
					});
		}

		if (msg instanceof RequestKeyboardInput) {
			fiBinding.chatEditText.setInputType(((RequestKeyboardInput) msg).getInputType());
			openKeyboard(fiBinding.chatEditText);
		}

		if (msg instanceof RequestUserInformation) {
			PrimaryDialog loadingDialog = new PrimaryDialog(ChatActivity.this);
			loadingDialog.setTitle(getString(R.string.registration_string_signature_verifying));
			loadingDialog.setDescription(getString(R.string.registration_string_wait));
			loadingDialog.show();

			Observable.interval(1, TimeUnit.SECONDS)
					.observeOn(AndroidSchedulers.mainThread())
					.first((long) 1)
					.subscribe(i -> {
						MessageBox.INSTANCE.add(new SignatureVerified());
						loadingDialog.dismiss();
					});
		}

		if (msg instanceof RequestSignature) {
			releaseControls();
			releaseAllControls();
			hideAppBar();
			hideStatusBar();
			binding.footer.setPadding(0, 0, 0, 0);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

			View signView = inflate(R.layout.chat_capture);
			binding.footer.addView(signView);
			signValidationFragment = new SignValidationFragment();
			signValidationFragment.setOnSignValidationListener(new SignValidationFragment.OnSignValidationListener() {
				@Override
				public void onValidation(boolean isValid) {

					if (isValid) {
						SucceededDialog dialog = new SucceededDialog(ChatActivity.this);
						dialog.setTitle(getString(R.string.setting_string_signature_verified));
						dialog.setDescription(getString(R.string.setting_string_authentication_complete));
						dialog.setButtonText(getString(R.string.setting_string_yes));
						dialog.setDoneListener(() -> {
							showAppBar();
							returnToInitialControl();
							showStatusBar();

							FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
							transaction.remove(signValidationFragment).commit();
							binding.chatView.scrollToBottom();
							setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

							MessageBox.INSTANCE.add(new RequestRemoveControls());
							MessageBox.INSTANCE.add(new SignatureVerified());
							dialog.dismiss();
						});
						dialog.showWithRatio(0.50f);
					} else {
						WarningDialog warningDialog = new WarningDialog(ChatActivity.this);
						warningDialog.setTitle(getString(R.string.setting_string_click_re_try_button));
						warningDialog.setButtonText(getString(R.string.setting_string_re_try));
						warningDialog.setDoneListener(() -> Observable.interval(1200, TimeUnit.MILLISECONDS)
								.observeOn(AndroidSchedulers.mainThread())
								.subscribe(aLong -> warningDialog.dismiss()));

						warningDialog.show();
					}

				}
			});


			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();


			tx.replace(R.id.chat_capture, signValidationFragment);
			tx.commit();
		}

		if (msg instanceof ShowPdfView) {
			ShowPdfView action = (ShowPdfView) msg;

			ContractPdfViewDialog dialog = new ContractPdfViewDialog(this);
			dialog.setTitle(action.getTitle());
			dialog.setStep(action.getStep());
			dialog.setPdfAssets(action.getPdfAsset());
			dialog.show();
		}

		if (msg instanceof RequestRemoveControls) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			if (signValidationFragment != null) {
				transaction.remove(signValidationFragment);
			}
			transaction.commit();

			this.returnToInitialControl();
		}

		if (msg instanceof RequestTransferUI) {
			releaseAllControls();

			ctBinding.editMoney.setEnabled(true);
			ctBinding.editMoney.requestFocus();

			binding.footer.addView(ctBinding.getRoot());
		}

		if (msg instanceof RequestSelectContact) {
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, RESULT_PICK_CONTACT);
		}

		if (msg instanceof DismissKeyboard) {
			returnToInitialControl();
		}

		if (msg instanceof RequestContactPermission) {
			if (!hasContactPermission())
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 100);
		}
	}

	private void showStatusBar() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	private void showAppBar() {
		binding.appbar.setVisibility(View.VISIBLE);
	}

	private void hideAppBar() {
		binding.appbar.setVisibility(View.GONE);
	}

	private void hideStatusBar() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	public void onSendButtonClickEvent() {
		String msg = fiBinding.chatEditText.getText().toString();
		if (fiBinding.chatEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD))
			msg = msg.replaceAll(".", "*");

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
			runOnUiThread(() -> {
				binding.chatView.scrollToBottom();
			});
		}
	}

	private void chatEditFieldTextChanged(CharSequence value) {
		boolean enabled = !value.toString().isEmpty();
		fiBinding.sendButton.setEnabled(enabled);

		if (enabled) {
			fiBinding.sendButton.setImageResource(R.drawable.btn_send_50);
		} else {
			fiBinding.sendButton.setImageResource(R.drawable.btn_mike_50);
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
		binding.chatView.scrollToBottom();
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

		ctBinding = ChatTransferBinding.bind(transferView);
		ctBinding.gvKeypad.addManagableTextField(ctBinding.editMoney);
		ctBinding.gvKeypad.setLengthLimit(10);
		ctBinding.gvKeypad.onComplete(() -> {
			// 잔액
			String moneyAsString = ctBinding.editMoney.getText().toString();

			ctBinding.editMoney.setText("");
			this.returnToInitialControl();

			MessageBox.INSTANCE.add(new SendMessage(moneyAsString + " won"));
		});

		binding.footer.addView(footerInputs);
	}

	/**
	 * 키보드 이외에 화면을 터치한 경우 자동으로 키보드를 dismiss 하기 위한 소스
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
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

	private IDCardInfo getIdCardInfo(OcrResult ocrResult) {
		byte[] idcardImage = ocrResult.getIdcardImg();
		Bitmap image = BitmapFactory.decodeByteArray(idcardImage, 0, idcardImage.length);
		String type = (ocrResult.getIdcardType().equals("1")) ? "주민등록증" : "운전면허증";
		return new IDCardInfo(type, ocrResult.getName(), CryptoUtil.transJuminStr(ocrResult.getIdcardNum()), ocrResult.getIssueDate(), image);
	}

	private void startOcrModule() {
		OcrParam ocrParam = new OcrParam();
		//촬영 시간
		ocrParam.setRetrytime(15000);
		//포커스 후 촬영횟수
		ocrParam.setFocusCount(4);
		//주민번호 마스크 여부
		ocrParam.setMaskIdnum(true);
		//암호일련번호 옵션 (0 = 미적용 , 1 = 적용)
		ocrParam.setLicenseCode(1);

		LibraryInterface.startOcr(this, "", "", ocrParam);
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
		fiBinding.chatEditText.setInputType(InputType.TYPE_CLASS_TEXT);
		dismissKeyboard(fiBinding.chatEditText);
		hideExControl();
	}

	private void releaseAllControls() {
		fiBinding.chatEditText.setInputType(InputType.TYPE_CLASS_TEXT);
		dismissKeyboard(fiBinding.chatEditText);
		binding.footer.removeAllViews();
	}

	private void returnToInitialControl() {
		releaseAllControls();
		binding.footer.addView(footerInputs);

		binding.footer.setPadding(Converter.dpToPx(5),
				Converter.dpToPx(5),
				Converter.dpToPx(5),
				Converter.dpToPx(5));
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

		LibraryInterface.unregisterOcrResultLinstener();
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

	private boolean hasContactPermission() {
		return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
	}

}