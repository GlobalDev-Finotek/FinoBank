package finotek.global.dev.talkbank_ca.setting;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivitySettingDetailBinding;
import finotek.global.dev.talkbank_ca.setting.abnormal.AbnormalTransactionAuthFragment;
import finotek.global.dev.talkbank_ca.setting.context_aware.ContextAwareAuthFragment;
import finotek.global.dev.talkbank_ca.setting.cost_auth.CostAuthFragment;
import finotek.global.dev.talkbank_ca.user.UserInfoFragment;

public class SettingDetailActivity extends AppCompatActivity {

	ActivitySettingDetailBinding binding;
	int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_detail);

		setSupportActionBar(binding.toolbar);
		binding.ibBack.setOnClickListener(v -> onBackPressed());
		binding.appbar.setOutlineProvider(null);
		Intent intent = getIntent();
		Bundle b = intent.getExtras();

		try {
			Fragment contentFragment = getContentFragment(b.getInt("type"));
			String title = contentFragment.getArguments().getString("title");
			getSupportActionBar().setTitle("");
			binding.toolbarTitle.setText(title);

			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.add(R.id.fl_content, contentFragment);
			transaction.commit();

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	private Fragment getContentFragment(int type) {
		switch (type) {

			case PageType.ABNORMAL_TRANSACTION:
				return AbnormalTransactionAuthFragment.newInstance(getString(R.string.setting_string_suspicious_transaction_verification));

			case PageType.CONTEXT_AWARE:
				return ContextAwareAuthFragment.newInstance(getString(R.string.setting_string_context_auth));

			case PageType.COST_AUTH:
				return CostAuthFragment.newInstance(getString(R.string.setting_string_amount_auth));

			case PageType.USER_INFO:
				return UserInfoFragment.newInstance(getString(R.string.setting_string_user_information));
			default:
				return null;
		}
	}


}
