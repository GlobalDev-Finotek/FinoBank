package finotek.global.dev.talkbank_ca.user.sign;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.chat.ChatActivity;
import finotek.global.dev.talkbank_ca.databinding.ActivitySignRegistartionBinding;
import finotek.global.dev.talkbank_ca.user.dialogs.PrimaryDialog;
import finotek.global.dev.talkbank_ca.user.dialogs.SucceededDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;


public class SignRegistrationActivity extends AppCompatActivity {

	private ActivitySignRegistartionBinding binding;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_registartion);
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("");
		binding.toolbarTitle.setText(getString(R.string.registration_string_signnature));
		binding.appbar.setOutlineProvider(null);
		binding.ibBack.setOnClickListener(v -> onBackPressed());

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Intent intent = getIntent();

		try {

			int mode = intent.getIntExtra("mode", SignMode.ONCE);

			if (mode == SignMode.ONCE) {
//				binding.tvInst1.setText(getString(R.string.registration_string_fingertip_use));
				binding.tvInst1.setText(getString(R.string.dialog_string_finger_tip_sign_user_register));
				binding.llInst2.setVisibility(View.GONE);
				binding.llInst3.setVisibility(View.GONE);
			} else if (mode == SignMode.TWICE) {

			}

			BaseSignRegisterFragment signRegistFragment = getSignRegisterFragment(mode);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.add(R.id.fl_content, signRegistFragment);
			transaction.commit();

			signRegistFragment.setOnSizeControlClick(size -> {
				if (size == BaseSignRegisterFragment.CanvasSize.LARGE) {
					LinearLayout.LayoutParams fl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);
					binding.flContent.setLayoutParams(fl);
					binding.llInstWrapper.setVisibility(View.GONE);
					binding.appbar.setVisibility(View.GONE);
				} else if (size == BaseSignRegisterFragment.CanvasSize.SMALL) {
					LinearLayout.LayoutParams fl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
							(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics()));
					binding.llInstWrapper.setVisibility(View.VISIBLE);
					binding.flContent.setLayoutParams(fl);
					binding.appbar.setVisibility(View.VISIBLE);
				}
			});


			Class nextClass = (Class) intent.getExtras().get("nextClass");
			signRegistFragment.setOnSaveListener(() -> {
				PrimaryDialog loadingDialog = new PrimaryDialog(SignRegistrationActivity.this);
				loadingDialog.setTitle(getString(R.string.registration_string_signature_verifying));
				loadingDialog.setDescription(getString(R.string.registration_string_wait));
				loadingDialog.show();

				Observable.interval(1500, TimeUnit.MILLISECONDS)
						.observeOn(AndroidSchedulers.mainThread())
						.first((long) 1)
						.subscribe(i -> {
							loadingDialog.dismiss();

							SucceededDialog dialog = new SucceededDialog(SignRegistrationActivity.this);
							dialog.setTitle(getString(R.string.setting_string_signature_verified));
							dialog.setDescription(getString(R.string.setting_string_authentication_complete));
							dialog.setButtonText(getString(R.string.setting_string_yes));
							dialog.setDoneListener(() -> {
								Intent intent2 = new Intent(SignRegistrationActivity.this, nextClass);
								if (nextClass.equals(ChatActivity.class)) {
									intent2.putExtra("isSigned", true);
								}
								intent2.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
								intent2.putExtras(intent.getExtras());
								startActivity(intent2);
								finish();
							});
							dialog.show();
						});
			});
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private BaseSignRegisterFragment getSignRegisterFragment(int mode) {
		BaseSignRegisterFragment signRegistFragment = null;

		if (mode == SignMode.ONCE) {
			signRegistFragment = new OneStepSignRegisterFragment();
		} else if (mode == SignMode.TWICE) {
			signRegistFragment = new TwoStepSignRegistFragment();
		}

		if (signRegistFragment == null) throw new IllegalArgumentException();

		return signRegistFragment;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		int orientation = newConfig.orientation;

		switch (orientation) {

			case Configuration.ORIENTATION_LANDSCAPE:
				binding.appbar.setVisibility(View.GONE);
				break;

			case Configuration.ORIENTATION_PORTRAIT:
				binding.appbar.setVisibility(View.VISIBLE);
				break;

		}
	}

	public static class SignMode {
		public static int ONCE = 1;
		public static int TWICE = 2;
	}
}