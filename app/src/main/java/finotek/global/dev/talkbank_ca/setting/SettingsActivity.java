package finotek.global.dev.talkbank_ca.setting;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import finotek.global.dev.talkbank_ca.R;
import finotek.global.dev.talkbank_ca.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

	private ActivitySettingsBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
		setSupportActionBar(binding.toolbar);
		getSupportActionBar().setTitle("설정");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
		binding.tabs.addTab(binding.tabs.newTab().setText("사용자 정보"));
		binding.tabs.addTab(binding.tabs.newTab().setText("맥락 인증"));
		binding.tabs.addTab(binding.tabs.newTab().setText("금액별 인증"));
		binding.tabs.addTab(binding.tabs.newTab().setText("이상거래 시 인증"));

		SettingPageAdapter pageAdapter = new SettingPageAdapter(getFragmentManager());
		binding.viewpager.setAdapter(pageAdapter);
		binding.viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabs));
		binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				binding.viewpager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}
}
