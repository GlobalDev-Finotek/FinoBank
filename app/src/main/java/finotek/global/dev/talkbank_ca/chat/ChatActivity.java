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
import android.graphics.Rect;
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

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.app.MyApplication;
import finotek.global.dev.talkbank_ca.base.mvp.event.RxEventBus;
import finotek.global.dev.talkbank_ca.chat.context_log.ContextApp;
import finotek.global.dev.talkbank_ca.chat.context_log.ContextCall;
import finotek.global.dev.talkbank_ca.chat.context_log.ContextLocation;
import finotek.global.dev.talkbank_ca.chat.context_log.ContextLogService;
import finotek.global.dev.talkbank_ca.chat.context_log.ContextSms;
import finotek.global.dev.talkbank_ca.chat.context_log.ContextTotal;
import finotek.global.dev.talkbank_ca.chat.messages.MessageEmitted;
import finotek.global.dev.talkbank_ca.chat.messages.ReceiveMessage;
import finotek.global.dev.talkbank_ca.chat.messages.RequestContactPermission;
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
import finotek.global.dev.talkbank_ca.chat.messages.control.RecoMenuRequest;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI_v1;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI_v2;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.RequestTransferUI_v3;
import finotek.global.dev.talkbank_ca.chat.messages.transfer.TransferButtonPressed;
import finotek.global.dev.talkbank_ca.chat.messages.ui.IDCardInfo;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestPhoto;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestRemoveControls;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestSignature;
import finotek.global.dev.talkbank_ca.chat.messages.ui.RequestTakeIDCard;
import finotek.global.dev.talkbank_ca.chat.messages.ui.TransferRequestSignature;
import finotek.global.dev.talkbank_ca.chat.storage.TransactionDB;
import finotek.global.dev.talkbank_ca.databinding.ActivityChatBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatExtendedControlBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatFooterInputBinding;
import finotek.global.dev.talkbank_ca.databinding.ChatTransferBinding;
import finotek.global.dev.talkbank_ca.inject.component.ChatComponent;
import finotek.global.dev.talkbank_ca.inject.component.DaggerChatComponent;
import finotek.global.dev.talkbank_ca.inject.module.ActivityModule;
import finotek.global.dev.talkbank_ca.model.DBHelper;
import finotek.global.dev.talkbank_ca.model.User;
import finotek.global.dev.talkbank_ca.setting.SettingsActivity;
import finotek.global.dev.talkbank_ca.user.CapturePicFragment;
import finotek.global.dev.talkbank_ca.user.dialogs.DangerDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.PdfViewDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.PrimaryDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.SucceededDialog;
import finotek.global.dev.talkbank_ca.user.sign.BaseSignRegisterFragment;
import finotek.global.dev.talkbank_ca.user.sign.OneStepSignRegisterFragment;
import finotek.global.dev.talkbank_ca.user.sign.TransferSignRegisterFragment;
import finotek.global.dev.talkbank_ca.util.Converter;
import finotek.global.dev.talkbank_ca.util.KeyboardUtils;
import globaldev.finotek.com.logcollector.Finopass;
import globaldev.finotek.com.logcollector.api.score.BaseScoreParam;
import globaldev.finotek.com.logcollector.api.score.ContextScoreResponse;
import globaldev.finotek.com.logcollector.model.ActionType;
import globaldev.finotek.com.logcollector.model.MessageLog;
import globaldev.finotek.com.logcollector.model.ValueQueryGenerator;
import globaldev.finotek.com.logcollector.util.AesInstance;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoGetter;
import globaldev.finotek.com.logcollector.util.userinfo.UserInfoGetterImpl;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.realm.Realm;
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
	private OneStepSignRegisterFragment signRegistFragment;
	private TransferSignRegisterFragment transferSignRegistFragment;

	private BroadcastReceiver receiver;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int smsPermissionCheck = ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_SMS);
		if (smsPermissionCheck != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(ChatActivity.this,
					new String[]{Manifest.permission.READ_SMS},
					PERMISSION_REQUEST_READ_SMS);
		}

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

		if (intent != null) {
			boolean isSigned = intent.getBooleanExtra("isSigned", false);
			mainScenario = new MainScenario_v2(this, binding.chatView, eventBus, dbHelper, isSigned);
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


		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("chat.ContextLog.ContextLogService");

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String askType = intent.getStringExtra("askType");

				ArrayList<ValueQueryGenerator> queryMaps = new ArrayList<>();

				if (askType.equals("smsLog") || askType.equals("totalLog")) {
					List<MessageLog> smsLogData = intent.getParcelableArrayListExtra("smsLog");
					queryMaps.addAll(smsLogData);
					Log.d("FINOPASS", "sms logs: " + smsLogData);
				}

				if (askType.equals("callLog") || askType.equals("totalLog")) {
					List<MessageLog> callLogData = intent.getParcelableArrayListExtra("callLog");
					queryMaps.addAll(callLogData);
					Log.d("FINOPASS", "call logs: " + callLogData);
				}

				if (askType.equals("locationLog") || askType.equals("totalLog")) {
					List<MessageLog> locationLogData = intent.getParcelableArrayListExtra("locationLog");
					queryMaps.addAll(locationLogData);
					Log.d("FINOPASS", "location logs: " + locationLogData);
				}

				if (askType.equals("appLog") || askType.equals("totalLog")) {
					List<MessageLog> appLogData = intent.getParcelableArrayListExtra("appLog");
					queryMaps.addAll(appLogData);
					Log.d("FINOPASS", "app logs: " + appLogData);
				}

				Finopass.getInstance(ChatActivity.this)
						.getScore(queryMaps)
						.subscribe(
								scoreParams -> {
									Log.d("FINOPASS", "FINOPASS in ChatActivity");
									Log.d("FINOPASS", "FINOPASS in ChatActivity: Score Params: " + scoreParams.toString());

									String message = "";
									if (scoreParams.messages == null || scoreParams.messages.size() == 0) {
										message = getString(R.string.dialog_chat_contextlog_result_nothing);
									} else {
										message = buildScoreMessages(scoreParams);
									}

									User user = Realm.getDefaultInstance().where(User.class).findAll().last();
                                    String userName = "";
                                    if(user != null)
                                        userName = user.getName();

									MessageBox.INSTANCE.addAndWait(
											new ReceiveMessage(getString(R.string.dialog_chat_contextlog_result_message, userName, scoreParams.finalScore * 100, message)),
											new Done()
									);
								},
								throwable -> {
									System.out.println(throwable);
								}, () -> {

								});

			}
		};
		registerReceiver(receiver, intentFilter);

	}

	@Override
	protected void onPause() {
		super.onPause();
		// hideExControl();
	}

	private void onNewMessageUpdated(Object msg) {
		if (msg instanceof ContextTotal) {
			Intent intent = new Intent(this, ContextLogService.class);
			intent.putExtra("askType", "totalLog");
			startService(intent);
		}

		if (msg instanceof ContextSms) {
			Intent intent = new Intent(this, ContextLogService.class);
			intent.putExtra("askType", "smsLog");
			startService(intent);
		}

		if (msg instanceof ContextCall) {
			Intent intent = new Intent(this, ContextLogService.class);
			intent.putExtra("askType", "callLog");
			startService(intent);
		}
		if (msg instanceof ContextLocation) {
			Intent intent = new Intent(this, ContextLogService.class);
			intent.putExtra("askType", "locationLog");
			startService(intent);
		}
		if (msg instanceof ContextApp) {
			Intent intent = new Intent(this, ContextLogService.class);
			intent.putExtra("askType", "appLog");
			startService(intent);
		}


		if (msg instanceof RequestPhoto) {

			hideAppBar();
			hideStatusBar();
			binding.footer.setPadding(0, 0, 0, 0);
			releaseControls();
			releaseAllControls();

			View captureView = inflate(R.layout.chat_capture);
			binding.footer.addView(captureView);
			capturePicFragment = CapturePicFragment.newInstance();
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			capturePicFragment.takePicture(path -> {
				MessageBox.INSTANCE.addAndWait(
						new IDCardInfo("주민등록증", "김우섭", "660103-1111111", "2016.3.10", ""),
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

		if (msg instanceof RequestTakeAnotherIDCard) {
			releaseControls();
			releaseAllControls();
			binding.footer.setPadding(0, 0, 0, 0);
			hideStatusBar();
			hideAppBar();

			View captureView = inflate(R.layout.chat_capture);
			binding.footer.addView(captureView);
			capturePicFragment = CapturePicFragment.newInstance();
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			capturePicFragment.takePicture(path -> {
				MessageBox.INSTANCE.addAndWait(
						new IDCardInfo("주민등록증", "김우섭", "660103-1111111", "2016.3.10", path),
						RecoMenuRequest.buildYesOrNo(getApplicationContext(), getResources().getString(R.string.main_string_v2_login_electricity_additional_picture))
				);
				this.returnToInitialControl();
				showAppBar();
				showStatusBar();

				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.remove(capturePicFragment).commit();

				binding.chatView.scrollToBottom();
			});

			tx.replace(R.id.chat_capture, capturePicFragment);
			tx.commit();
		}

		if (msg instanceof RequestTakeIDCard) {
			releaseControls();
			releaseAllControls();
			binding.footer.setPadding(0, 0, 0, 0);
			hideAppBar();
			hideStatusBar();

			View captureView = inflate(R.layout.chat_capture);
			binding.footer.addView(captureView);
			capturePicFragment = CapturePicFragment.newInstance();
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			capturePicFragment.takePicture(path -> {
				MessageBox.INSTANCE.addAndWait(
						new IDCardInfo("주민등록증", "김우섭", "660103-1111111", "2016.3.10", ""),
						RecoMenuRequest.buildYesOrNo(getApplicationContext(), getResources().getString(R.string.main_string_v2_login_electricity_additional_picture))
				);

				MessageBox.INSTANCE.add(new ReceiveMessage(getString(R.string.dialog_chat_correct_information)));

				showAppBar();
				this.returnToInitialControl();
				showStatusBar();

				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.remove(capturePicFragment).commit();

				binding.chatView.scrollToBottom();
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
			hideAppBar();
			hideStatusBar();
			binding.footer.setPadding(0, 0, 0, 0);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

			View signView = inflate(R.layout.chat_capture);
			binding.footer.addView(signView);

			signRegistFragment = new OneStepSignRegisterFragment();

			FragmentTransaction tx = getFragmentManager().beginTransaction();
			signRegistFragment.setOnSaveListener(() -> {
				PrimaryDialog loadingDialog = new PrimaryDialog(ChatActivity.this);
				loadingDialog.setTitle(getString(R.string.registration_string_signature_verifying));
				loadingDialog.setDescription(getString(R.string.registration_string_wait));
				loadingDialog.showWithRatio(0.50f);

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

		if (msg instanceof TransferRequestSignature) {
			releaseControls();
			releaseAllControls();
			hideAppBar();
			hideStatusBar();
			binding.footer.setPadding(0, 0, 0, 0);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

			View signView = inflate(R.layout.chat_capture);
			binding.footer.addView(signView);

			transferSignRegistFragment = new TransferSignRegisterFragment();

			FragmentTransaction tx = getFragmentManager().beginTransaction();
			transferSignRegistFragment.setOnSaveListener(() -> {
				PrimaryDialog loadingDialog = new PrimaryDialog(ChatActivity.this);
				loadingDialog.setTitle(getString(R.string.registration_string_signature_verifying));
				loadingDialog.setDescription(getString(R.string.registration_string_wait));
				loadingDialog.showWithRatio(0.50f);

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
						}, throwable -> {
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
			ctBinding.editMoney.setEnabled(false);
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

	private String buildScoreMessages(ContextScoreResponse scoreResponse) throws Exception {
        String messages = "";
        int size = scoreResponse.messages.size();
        for(int i = 0; i < size; i++) {
            BaseScoreParam msg = scoreResponse.messages.get(i);
            Log.d("FINOPASS", msg.toString());
            UserInfoGetter uig = new UserInfoGetterImpl(getApplication(), getSharedPreferences("prefs", Context.MODE_PRIVATE));
            AesInstance aes = AesInstance.getInstance(uig.getUserKey().substring(0, 16).getBytes());

            switch(msg.type) {
                case ActionType.GATHER_APP_USAGE_LOG:
                    String appName = aes.decText(msg.param.get("appName"));
                    messages += (i+1) + ": " + getString(R.string.contextlog_result_message_app_usage, appName, msg.rank, msg.beforeTime, msg.score);
                    break;
            }

            if(i != size-1)
                messages += "\n\n";

            String appName = aes.decText(msg.param.get("appName"));
            Log.d("FINOPASS", "params: " + msg.param + ", appName: " + appName);
        }

        return messages;
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