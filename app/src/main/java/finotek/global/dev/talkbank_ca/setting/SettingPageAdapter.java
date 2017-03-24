package finotek.global.dev.talkbank_ca.setting;

import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import finotek.global.dev.talkbank_ca.user.UserInfoFragment;

/**
 * Created by magyeong-ug on 21/03/2017.
 */

public class SettingPageAdapter extends FragmentStatePagerAdapter {

	private final int NUM_PAGES = 4;

	public SettingPageAdapter(android.app.FragmentManager fm) {
		super(fm);
	}

	@Override
	public android.app.Fragment getItem(int position) {
		switch (position) {
			case 0:
				return UserInfoFragment.newInstance("");
			case 1:
				return new ContextAwareAuthFragment();
			case 2:
				return new CostAuthFragment();
			case 3:
				return new AbnormalTransactionAuthFragment();
			default:
				return null;
		}
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}
}
