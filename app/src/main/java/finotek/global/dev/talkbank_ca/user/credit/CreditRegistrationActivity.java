package finotek.global.dev.talkbank_ca.user.credit;

import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivityCreditRegistrationBinding;
import finotek.global.dev.talkbank_ca.model.CreditCard;
import finotek.global.dev.talkbank_ca.user.CapturePicFragment;

public class CreditRegistrationActivity extends AppCompatActivity implements CreditRegistrationView {

	private ActivityCreditRegistrationBinding binding;
	private boolean isCaptureDone;
	private CapturePicFragment capturePicFragment = new CapturePicFragment();
	private CreditRegistration presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_credit_registration);

		presenter = new CreditRegistrationImpl(this);

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.fl_cam, capturePicFragment);
		transaction.commit();

		RxView.clicks(binding.btnTakePic)
				.throttleFirst(200, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
						capturePicFragment.takePicture(path -> presenter.takePic(path));
				});


		RxView.clicks(binding.btnRecapture)
				.throttleFirst(300, TimeUnit.MILLISECONDS)
				.subscribe(aVoid -> {
					presenter.recapture();
				});

	}

	@Override
	public void displayCreditCardInfo(CreditCard card) {
		binding.incWidget.getRoot().setVisibility(View.VISIBLE);
		binding.incWidget.setCreditCard(card);
	}

	@Override
	public void displayOnCaptureDone() {
		binding.btnRecapture.setVisibility(View.VISIBLE);
		setBtnCaptureText(R.string.string_complete);
	}

	@Override
	public void displayOnRecapture() {
		binding.btnRecapture.setVisibility(View.GONE);
		setBtnCaptureText(R.string.take_pic);
	}

	@Override
	public void unlockCamera() {
		capturePicFragment.unlockCamera();
	}

	@Override
	public void setBtnCaptureText(int stringId) {
		binding.btnTakePic.setText(getString(stringId));
	}
}
