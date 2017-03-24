package finotek.global.dev.talkbank_ca.setting;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivitySettingDetailBinding;
import finotek.global.dev.talkbank_ca.user.UserInfoFragment;

public class SettingDetailActivity extends AppCompatActivity {

	ActivitySettingDetailBinding binding;
	int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_detail);

		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

		Intent intent = getIntent();
		Bundle b = intent.getExtras();

		try {
			Fragment contentFragment = getContentFragment(b.getInt("type"));
			String title = contentFragment.getArguments().getString("title");
			getSupportActionBar().setTitle(title);

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
				return AbnormalTransactionAuthFragment.newInstance("이상 거래 시 인증");

			case PageType.CONTEXT_AWARE:
				return ContextAwareAuthFragment.newInstance("맥락 인증");

			case PageType.COST_AUTH:
				return CostAuthFragment.newInstance("금액 인증");

			case PageType.USER_INFO:
				return UserInfoFragment.newInstance("사용자 정보");
			default:
				return null;
		}
	}


}
