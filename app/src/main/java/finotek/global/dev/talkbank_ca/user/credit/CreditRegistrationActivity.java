package finotek.global.dev.talkbank_ca.user.credit;

import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityCreditRegistrationBinding;
import finotek.global.dev.talkbank_ca.model.CreditCard;
import finotek.global.dev.talkbank_ca.user.CapturePicFragment;
import finotek.global.dev.talkbank_ca.util.Converter;

public class CreditRegistrationActivity extends AppCompatActivity implements CreditRegistrationView {

	private ActivityCreditRegistrationBinding binding;
	private boolean isCaptureDone;
	private CapturePicFragment capturePicFragment = new CapturePicFragment();
	private CreditRegistration presenter;

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_credit_registration);
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("");
		binding.toolbarTitle.setText(getString(R.string.registration_string_credit_card_register));
		binding.appbar.setOutlineProvider(null);
		binding.ibBack.setOnClickListener(v -> onBackPressed());

		presenter = new CreditRegistrationImpl(this);
		presenter.attachView(this);

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.fl_cam, capturePicFragment);
		transaction.commit();

		capturePicFragment.takePicture(path -> {
			if (!isCaptureDone) {
				binding.llInstWrapper.setVisibility(View.GONE);
				presenter.takePic(path);
			} else {
				finish();
			}
			isCaptureDone = !isCaptureDone;
		});

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		binding.flCam.setLayoutParams(lp);
		binding.appbar.setVisibility(View.GONE);

		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				Converter.dpToPx(350));
		binding.flCam.setLayoutParams(lp2);
		binding.appbar.setVisibility(View.VISIBLE);


//		capturePicFragment.setOnSizeChangeListener(new CapturePicFragment.OnSizeChangeListener() {
//			@Override
//			public void onSizeFull() {
//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//						LinearLayout.LayoutParams.MATCH_PARENT);
//				binding.flCam.setLayoutParams(lp);
//				binding.appbar.setVisibility(View.GONE);
//			}
//
//			@Override
//			public void onSizeMinimize() {
//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//						Converter.dpToPx(350));
//				binding.flCam.setLayoutParams(lp);
//				binding.appbar.setVisibility(View.VISIBLE);
//			}
//		});

		RxView.clicks(binding.btnSave)
			.throttleFirst(200, TimeUnit.MILLISECONDS)
			.subscribe(aVoid -> {
				finish();
			});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		presenter.detachView();
	}

	@Override
	public void displayCreditCardInfo(CreditCard card) {
		binding.incWidget.getRoot().setVisibility(View.VISIBLE);
		binding.incWidget.setCreditCard(card);
		binding.btnSave.setVisibility(View.VISIBLE);
	}

}
