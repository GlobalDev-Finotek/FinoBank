package finotek.global.dev.talkbank_ca.user.credit;

import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
		presenter.attachView(this);

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.fl_cam, capturePicFragment);
		transaction.commit();

		capturePicFragment.takePicture(path -> presenter.takePic(path));

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
	}

}
