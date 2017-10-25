package finotek.global.dev.talkbank_ca.chat;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.finotek.finocr.common.CryptoUtil;
import com.finotek.finocr.listener.OcrResultLinstener;
import com.finotek.finocr.manager.LibraryInterface;
import com.finotek.finocr.vo.OcrParam;
import com.finotek.finocr.vo.OcrResult;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.chat.ContextLog.ContextLogReceiver;
import finotek.global.dev.talkbank_ca.chat.ContextLog.ContextLogType;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RemoteCallCompleted;
import finotek.global.dev.talkbank_ca.chat.messages.RequestContactPermission;
import finotek.global.dev.talkbank_ca.chat.messages.RequestRemoteCall;
import finotek.global.dev.talkbank_ca.chat.messages.RequestTakeAnotherIDCard;
import finotek.global.dev.talkbank_ca.chat.messages.SendMessage;
import finotek.global.dev.talkbank_ca.chat.messages.action.DismissKeyboard;
import finotek.global.dev.talkbank_ca.chat.messages.action.Done;
import finotek.global.dev.talkbank_ca.chat.messages.action.EnableToEditMoney;
import finotek.global.dev.talkbank_ca.chat.messages.action.RequestKeyboardInput;
import finotek.global.dev.talkbank_ca.chat.messages.action.ShowPdfView;
import finotek.global.dev.talkbank_ca.chat.messages.action.SignatureVerified;
import finotek.global.dev.talkbank_ca.chat.messages.contact.RequestSelectContact;
import finotek.global.dev.talkbank_ca.chat.messages.contact.SelectedContact;
import finotek.global.dev.talkbank_ca.chat.messages.context.ContextApp;
import finotek.global.dev.talkbank_ca.chat.messages.context.ContextCall;
import finotek.global.dev.talkbank_ca.chat.messages.context.ContextLocation;
import finotek.global.dev.talkbank_ca.chat.messages.context.ContextLogService;
import finotek.global.dev.talkbank_ca.chat.messages.context.ContextScoreReceived;
import finotek.global.dev.talkbank_ca.chat.messages.context.ContextSms;
import finotek.global.dev.talkbank_ca.chat.messages.context.ContextTotal;
import finotek.global.dev.talkbank_ca.chat.messages.context.CurrentAddressReceived;
import finotek.global.dev.talkbank_ca.chat.messages.context.RequestCurrentAddress;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.control.RecommendScenarioMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI_v1;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI_v2;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI_v3;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.messages.ui.IDCardInfo;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestPhoto;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignatureRegister;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignatureValidation;
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
import finotek.global.dev.talkbank_ca.user.dialogs.AgreementPdfViewDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.DangerDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.PrimaryDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.SucceededDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.WarningDialog;
import finotek.global.dev.talkbank_ca.user.remotecall.RemoteCallFragment;
import finotek.global.dev.talkbank_ca.user.sign.BaseSignRegisterFragment;
import finotek.global.dev.talkbank_ca.user.sign.SignRegisterFragment;
import finotek.global.dev.talkbank_ca.user.sign.SignValidationFragment;
import finotek.global.dev.talkbank_ca.util.ChatLocationListener;
import finotek.global.dev.talkbank_ca.util.ContextAuthPref;
import finotek.global.dev.talkbank_ca.util.Converter;
import finotek.global.dev.talkbank_ca.util.KeyboardUtils;
import globaldev.finotek.com.logcollector.Finopass;
import globaldev.finotek.com.logcollector.api.score.BaseScoreParam;
import globaldev.finotek.com.logcollector.api.score.ContextScoreResponse;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.model.ApplicationLog;
import globaldev.finotek.com.logcollector.model.CallHistoryLog;
import globaldev.finotek.com.logcollector.model.LocationLog;
import globaldev.finotek.com.logcollector.model.MessageLog;
import globaldev.finotek.com.logcollector.model.ValueQueryGenerator;
import globaldev.finotek.com.logcollector.util.AesInstance;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoGetter;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoGetterImpl;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

public class ChatActivity extends AppCompatActivity {
	static final int RESULT_PICK_CONTACT = 1;
	static final int PERMISSION_REQUEST_READ_SMS = 100;
	static final int PERMISSION_REQUEST_READ_CALL_LOG = 101;
	private static final int PERMISSION_CAMERA = 2;

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

	private MainScenario_v2 mainScenario;

	private CapturePicFragment capturePicFragment;
	private SignRegisterFragment signRegistFragment;
	private SignValidationFragment transferSignRegistFragment;
	private RemoteCallFragment remoteCallFragment;

	private BroadcastReceiver receiver;
	private boolean isFirstAuth = true;
	private OcrResultLinstener ocrResultListener = new OcrResultLinstener() {
		@Override
		public void onOcrSuccess(OcrResult ocrResult) {
			LibraryInterface.setOcrResult(ocrResult);
			MessageBox.INSTANCE.addAndWait(
					getIdCardInfo(ocrResult),
					RecoMenuRequest.buildYesOrNo(getApplicationContext(), getResources().getString(R.string.main_string_v2_login_electricity_additional_picture))
			);

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
		// TODO 아이템 간 패딩 정리
		// binding.chatView.addItemDecoration(new ViewItemD3ecoration());

		binding.ibMenu.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, SettingsActivity.class)));
		preInitControlViews();

		// keyboard event
		KeyboardUtils.addKeyboardToggleListener(this, isVisible -> {
			if (isVisible)
				binding.chatView.scrollToBottom();
		});

		initializeMessageBox();

		// initialize score receiver
		receiveScore();

		// register ocr listener
		LibraryInterface.registerOcrResultLinstener(ocrResultListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// hideExControl();
	}

	private void onNewMessageUpdated(Object msg) {
		boolean isContextAuthAgreed = getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean(getString(R.string.splash_is_auth_agree), false);

		if (msg instanceof ContextTotal && isContextAuthAgreed) {
            fetchContextLogData(ContextLogType.total);
		}

		if (msg instanceof ContextSms && isContextAuthAgreed) {
            fetchContextLogData(ContextLogType.sms);
		}

		if (msg instanceof ContextCall && isContextAuthAgreed) {
            fetchContextLogData(ContextLogType.call);
		}
		if (msg instanceof ContextLocation && isContextAuthAgreed) {
            fetchContextLogData(ContextLogType.location);
		}
		if (msg instanceof ContextApp && isContextAuthAgreed) {
			fetchContextLogData(ContextLogType.app);
		}

		if (msg instanceof RequestPhoto) {
			this.prepareForFullScreen();
			populateCaptureView();
		}

		if (msg instanceof RequestTakeAnotherIDCard) {
			this.prepareForFullScreen();
			populateCaptureView();
		}

		if (msg instanceof RequestRemoteCall) {
			this.prepareForFullScreen();

			View captureView = inflate(R.layout.chat_capture);
			binding.footer.addView(captureView);

			remoteCallFragment = new RemoteCallFragment();
			remoteCallFragment.setVideoURL("android.resource://" + getPackageName() + "/" + R.raw.lady_teller);
			remoteCallFragment.setStopListener(() -> {
				MessageBox.INSTANCE.add(new RemoteCallCompleted(), 500);
			});

			FragmentTransaction tx = getFragmentManager().beginTransaction();
			tx.replace(R.id.chat_capture, remoteCallFragment);
			tx.commit();
		}

		if (msg instanceof RemoteCallCompleted) {
			showAppBar();
			this.returnToInitialControl();
			showStatusBar();

			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.remove(remoteCallFragment).commit();
			binding.chatView.scrollToBottom();
		}

		if (msg instanceof RequestTakeIDCard) {
			startOcrModule();
		}

		if (msg instanceof RequestKeyboardInput) {
			openKeyboard(fiBinding.chatEditText);
		}


		if (msg instanceof RequestSignatureRegister) {
			this.prepareForFullScreen();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

			View signView = inflate(R.layout.chat_capture);
			binding.footer.addView(signView);

			signRegistFragment = new SignRegisterFragment();

			FragmentTransaction tx = getFragmentManager().beginTransaction();
			signRegistFragment.setOnSignValidationListener((similarity) -> {
				PrimaryDialog loadingDialog = new PrimaryDialog(ChatActivity.this);
				loadingDialog.setTitle(getString(R.string.registration_string_signature_registering));
				loadingDialog.setDescription(getString(R.string.registration_string_wait));
				loadingDialog.showWithRatio(0.50f);

				Observable.interval(1, TimeUnit.SECONDS)
						.observeOn(AndroidSchedulers.mainThread())
						.first((long) 1)
						.subscribe(i -> {

							loadingDialog.dismiss();

							SucceededDialog dialog = new SucceededDialog(ChatActivity.this);
							dialog.setDescription(getString(R.string.registration_string_register_success));
							dialog.setTitle(getString(R.string.setting_string_registered_signature));
							dialog.setButtonText(getString(R.string.setting_string_yes));
							dialog.setDoneListener(() -> {
								MessageBox.INSTANCE.add(new SignatureVerified());
								returnToInitialControl();

								showAppBar();
								showStatusBar();

								FragmentTransaction transaction = getFragmentManager().beginTransaction();
								transaction.remove(signRegistFragment).commit();

								dialog.dismiss();

								returnToInitialControl();

								binding.chatView.scrollToBottom();
								setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
							});
							dialog.showWithRatio(0.50f);

						}, throwable -> {
							Log.d("Sign Register", throwable.getMessage());
						});
			});


			tx.replace(R.id.chat_capture, signRegistFragment);
			tx.commit();
		}

		if (msg instanceof RequestSignatureValidation) {
			this.prepareForFullScreen();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

			View signView = inflate(R.layout.chat_capture);
			binding.footer.addView(signView);

			transferSignRegistFragment = new SignValidationFragment();
			FragmentTransaction tx = getFragmentManager().beginTransaction();

			transferSignRegistFragment.setOnSignValidationListener((similarity) -> {
				PrimaryDialog loadingDialog = new PrimaryDialog(ChatActivity.this);
				loadingDialog.setTitle(getString(R.string.registration_string_signature_verifying));
				loadingDialog.setDescription(getString(R.string.registration_string_wait));
				loadingDialog.showWithRatio(0.50f);

				Observable.interval(600, TimeUnit.MILLISECONDS)
						.observeOn(AndroidSchedulers.mainThread())
						.first((long) 1)
						.subscribe(i -> {
							loadingDialog.dismiss();

							if (similarity / 100 > 70) {
								SucceededDialog dialog = new SucceededDialog(ChatActivity.this);
								dialog.setTitle(getString(R.string.setting_string_signature_verified));
								dialog.setDescription(getString(R.string.setting_string_authentication_complete));
								dialog.setButtonText(getString(R.string.setting_string_yes));
								dialog.setDoneListener(() -> {
									MessageBox.INSTANCE.add(new SignatureVerified());
									returnToInitialControl();

									showAppBar();
									showStatusBar();

									FragmentTransaction transaction = getFragmentManager().beginTransaction();
									transaction.remove(transferSignRegistFragment).commit();

									dialog.dismiss();

									returnToInitialControl();

									binding.chatView.scrollToBottom();
									setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

								});
								dialog.showWithRatio(0.50f);
							} else {
								transferSignRegistFragment.init();
								MessageBox.INSTANCE.addAndWait(new RequestSignatureValidation());
								WarningDialog warningDialog = new WarningDialog(ChatActivity.this);
								warningDialog.setTitle(getString(R.string.setting_string_click_re_try_button));
								warningDialog.setButtonText(getString(R.string.setting_string_re_try));
								warningDialog.setDoneListener(() -> Observable.interval(1200, TimeUnit.MILLISECONDS)
										.observeOn(AndroidSchedulers.mainThread())
										.subscribe(aLong -> warningDialog.dismiss()));

								warningDialog.show();


							}


						}, throwable -> {
							Log.d("Sign Validation", throwable.getMessage());
						});
			});

			transferSignRegistFragment.setOnSizeControlClick(new BaseSignRegisterFragment.OnSizeControlClick() {

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

			tx.replace(R.id.chat_capture, transferSignRegistFragment);
			tx.commit();
		}

		if (msg instanceof RequestTransferUI) {
			releaseAllControls();

			int balance = TransactionDB.INSTANCE.getMainBalance();
			ctBinding.balance.setText(NumberFormat.getNumberInstance().format(balance));
			ctBinding.editMoney.setEnabled(((RequestTransferUI) msg).isEnabled());

			if (((RequestTransferUI) msg).isEnabled()) {
				ctBinding.editMoney.requestFocus();
				ctBinding.gvKeypad.setLengthLimit(7);
			}

			binding.footer.addView(ctBinding.getRoot());
		}

		if (msg instanceof RequestTransferUI_v1) {
			releaseAllControls();

			int balance = TransactionDB.INSTANCE.getFirstAlternativeBalance();
			ctBinding.balance.setText(NumberFormat.getNumberInstance().format(balance));
			ctBinding.editMoney.setEnabled(false);
			binding.footer.addView(ctBinding.getRoot());
		}

		if (msg instanceof RequestTransferUI_v2) {
			releaseAllControls();

			int balance = TransactionDB.INSTANCE.getSecondAlternativeBalance();
			ctBinding.balance.setText(NumberFormat.getNumberInstance().format(balance));
			ctBinding.editMoney.setEnabled(false);
			binding.footer.addView(ctBinding.getRoot());
		}

		if (msg instanceof RequestTransferUI_v3) {
			releaseAllControls();

			int balance = TransactionDB.INSTANCE.getThirdAlternativeBalance();
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
			AgreementPdfViewDialog dialog = new AgreementPdfViewDialog(this);
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

		if (msg instanceof RequestContactPermission) {
			if (!hasContactPermission())
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 100);
		}

		if (msg instanceof RequestCurrentAddress) {
			String address = getAddressName(getCurrentLocation());
			CurrentAddressReceived send = new CurrentAddressReceived(address);
			MessageBox.INSTANCE.add(send);
		}
	}

	private void populateCaptureView() {
		View captureView = inflate(R.layout.chat_capture);
		binding.footer.addView(captureView);
		capturePicFragment = CapturePicFragment.newInstance();
		FragmentTransaction tx = getFragmentManager().beginTransaction();
		capturePicFragment.takePicture(path -> {
			MessageBox.INSTANCE.addAndWait(
					getIdCardInfo(LibraryInterface.getOcrResult()),
					RecoMenuRequest.buildYesOrNo(getApplicationContext(), getResources().getString(R.string.main_string_v2_login_electricity_additional_picture))
			);

			showAppBar();
			showStatusBar();
			this.returnToInitialControl();

			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.remove(capturePicFragment).commit();
		});

		tx.replace(R.id.chat_capture, capturePicFragment);
		tx.commit();
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
		ImageView ivCtrl = (ImageView) footerInputs.findViewById(R.id.show_ex_control);
		ivCtrl.animate().rotation(0).setInterpolator(new LinearInterpolator())
				.setDuration(300);

	}

	private void showExControl() {
		isExControlAvailable = true;
		binding.footer.addView(exControlView, 0);
		ImageView ivCtrl = (ImageView) footerInputs.findViewById(R.id.show_ex_control);
		ivCtrl.animate().rotation(45).setInterpolator(new LinearInterpolator())
				.setDuration(300);

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

		// 하단 버튼 설정
		RxView.clicks(ecBinding.button1)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					MessageBox.INSTANCE.add(new SendMessage(getString(R.string.dialog_button_open_account), R.drawable.icon_stankbank01));
					hideExControl();
				});

		RxView.clicks(ecBinding.button2)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					MessageBox.INSTANCE.add(new SendMessage(getString(R.string.dialog_button_transfer), R.drawable.icon_stankbank02));
					hideExControl();
				});

		RxView.clicks(ecBinding.button3)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					MessageBox.INSTANCE.add(new SendMessage(getString(R.string.main_string_view_account_details), R.drawable.icon_stankbank03));
					hideExControl();
				});

		RxView.clicks(ecBinding.button4)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					MessageBox.INSTANCE.add(new SendMessage(getString(R.string.main_string_secured_mirocredit), R.drawable.icon_stankbank04));
					hideExControl();
				});

		RxView.clicks(ecBinding.button5)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					Intent intent = new Intent(ChatActivity.this, SettingsActivity.class);
					startActivity(intent);
					hideExControl();
				});

		RxView.clicks(ecBinding.button6)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					MessageBox.INSTANCE.add(new SendMessage(getString(R.string.main_button_send_the_conversation_to_e_mail), R.drawable.icon_stankbank06));
					hideExControl();
				});

		ctBinding = ChatTransferBinding.bind(transferView);
		ctBinding.gvKeypad.addManagableTextField(ctBinding.editMoney);
		ctBinding.gvKeypad.onComplete(() -> {
			if (!(TransactionDB.INSTANCE.getTxName() == null || TransactionDB.INSTANCE.getTxName().equals(""))) {
				// 잔액
				int balance = TransactionDB.INSTANCE.getMainBalance();
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
			}
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

		binding.footer.setPadding(Converter.dpToPx(5),
				Converter.dpToPx(5),
				Converter.dpToPx(5),
				Converter.dpToPx(5));
	}

	private View inflate(int layoutId) {
		ViewGroup parent = (ViewGroup) findViewById(android.R.id.content);
		return LayoutInflater.from(this).inflate(layoutId, parent, false);
	}


	private void receiveScore() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("chat.ContextLog.ContextLogService");

		receiver = new ContextLogReceiver(getApplication(), getApplicationContext(), (queryMaps) -> {
            Finopass.getInstance(ChatActivity.this)
                .getScore(queryMaps)
                .subscribe(
                        scoreParams -> {
                            Log.d("FINOPASS", "FINOPASS in ChatActivity: Score Params: " + scoreParams.toString());
                            Log.d("FINOPASS", "Your final score is : " + scoreParams.finalScore);
                            decodeScoreParams(scoreParams);

                            if (isFirstAuth) {
                                Log.d("FINOPASS", "첫번째 맥락인증 요청");
                                ContextAuthPref pref = new ContextAuthPref(getApplicationContext());
                                pref.save(scoreParams);

                                initializeMessageBox();
                                Log.d("FINOPASS", "시나리오 및 메시지 박스 생성");
                            } else {
                                Log.d("FINOPASS", "ContextScoreReceived emitted.");
                                MessageBox.INSTANCE.add(new ContextScoreReceived(scoreParams));
                            }
                        },
                        error -> {
                            WarningDialog dialog = new WarningDialog(getApplicationContext());
                            dialog.setTitle(getResources().getString(R.string.dialog_chat_contextlog_network_error_title));
                            dialog.setDescription(getResources().getString(R.string.dialog_chat_contextlog_network_failed));
                            dialog.setButtonText(getResources().getString(R.string.app_network_dialog_confirm));
                            dialog.setDoneListener(() -> {
                                dialog.dismiss();
                            });
                            dialog.show();
                        });
        });


		registerReceiver(receiver, intentFilter);
		boolean isContextAuthAgreed = getSharedPreferences("prefs", Context.MODE_PRIVATE)
				.getBoolean(getString(R.string.splash_is_auth_agree), false);

		if (isContextAuthAgreed) {
			Intent intent = new Intent(this, ContextLogService.class);
			intent.putExtra("askType", ContextLogType.total);
			startService(intent);
		} else {
			initializeMessageBox();
		}
	}

	private void fetchContextLogData(ContextLogType type) {
        Intent intent = new Intent(this, ContextLogService.class);
        intent.putExtra("askType", type);
        startService(intent);
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
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST_READ_SMS) {
			int callLogPermissionCheck = ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_CALL_LOG);
			if (callLogPermissionCheck != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(ChatActivity.this,
						new String[]{Manifest.permission.READ_CALL_LOG},
						PERMISSION_REQUEST_READ_CALL_LOG);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mainScenario.release();
		eventBus.clear();

		unregisterReceiver(receiver);
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

	private void initializeMessageBox() {

		// 메인 시나리오 세팅
		mainScenario = new MainScenario_v2(ChatActivity.this, binding.chatView);

		// 메시지 박스 설정
		MessageBox.INSTANCE.observable
				.flatMap(msg -> {
					if (msg instanceof EnableToEditMoney) {
						return Observable.just(msg)
								.observeOn(AndroidSchedulers.mainThread());
					} else {
						return Observable.just(msg)
								.delay(1, TimeUnit.SECONDS)
								.observeOn(AndroidSchedulers.mainThread());
					}
				})
				.subscribe(ChatActivity.this::onNewMessageUpdated,
						throwable -> Log.d("MessageBox", throwable.getMessage()));

		isFirstAuth = false;
	}

	private void decodeScoreParams(ContextScoreResponse scoreParams) throws Exception {
		UserInfoGetter uig = new UserInfoGetterImpl(getApplication(), getSharedPreferences("prefs", Context.MODE_PRIVATE));
		AesInstance aes = AesInstance.getInstance(uig.getUserKey().substring(0, 16).getBytes());

		if (scoreParams.messages != null) {
			for (BaseScoreParam msg : scoreParams.messages) {
				switch (msg.type) {
					case ActionType.GATHER_APP_USAGE_LOG:
						msg.param.put("appName", aes.decText(msg.param.get("appName")));
						break;
					case ActionType.GATHER_CALL_LOG:
						if (msg.param.get("targetName") != null && !msg.param.get("targetName").isEmpty())
							msg.param.put("targetName", aes.decText(msg.param.get("targetName")));
						break;
					case ActionType.GATHER_MESSAGE_LOG:
						if (msg.param.get("targetNumber") != null && !msg.param.get("targetNumber").isEmpty())
							msg.param.put("targetNumber", aes.decText(msg.param.get("targetNumber")));
						break;
				}
			}
		}
	}

	private String getAddressName(Location location) {
		try {
			Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
			List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			Log.d("FINOTEK", "addresses found: " + addresses);
			if (addresses != null && !addresses.isEmpty()) {
				return addresses.get(0).getAddressLine(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "지역을 찾을 수 없습니다.";
	}

	private Location getCurrentLocation() {
		Location currentLocation = null;
		LocationManager manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

		if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
			boolean isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (isGPSEnabled)
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, new ChatLocationListener());
			else if (isNetworkEnabled)
				manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, new ChatLocationListener());
		}

		List<String> providers = manager.getProviders(true);
		for (String prov : providers) {
			Location l = manager.getLastKnownLocation(prov);
			if (l != null) {
				currentLocation = l;
			}
		}

		return currentLocation;
	}

	private IDCardInfo getIdCardInfo(OcrResult ocrResult) {
		byte[] idcardImage = ocrResult.getIdcardImg();
		Bitmap image = BitmapFactory.decodeByteArray(idcardImage, 0, idcardImage.length);
		String type = (ocrResult.getIdcardType().equals("1")) ? "주민등록증" : "운전면허증";
		return new IDCardInfo(type, ocrResult.getName(), CryptoUtil.transJuminStr(ocrResult.getIdcardNum()), ocrResult.getIssueDate(), image);
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

	private void prepareForFullScreen() {
		hideAppBar();
		hideStatusBar();
		binding.footer.setPadding(0, 0, 0, 0);
		releaseControls();
		releaseAllControls();
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
}