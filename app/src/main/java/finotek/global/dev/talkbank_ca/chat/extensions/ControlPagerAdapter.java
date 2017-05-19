package finotek.global.dev.talkbank_ca.chat.extensions;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public class ControlPagerAdapter extends FragmentStatePagerAdapter {
	private final int NUM_PAGES = 2;
	private Runnable doOnControl;
	private Runnable settingControl;

	public ControlPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		if (position == 0) {
			ExtendedControl1 ec1 = new ExtendedControl1();
			ec1.setDoOnControl(doOnControl);
			return ec1;
		} else if (position == 1) {
			ExtendedControl2 ec2 = new ExtendedControl2();
			ec2.setDoOnControl(doOnControl);
			ec2.setSettingControl(settingControl);
			return ec2;
		} else {
			return null;
		}
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}

	public void setDoOnControl(Runnable doOnControl) {
		this.doOnControl = doOnControl;
	}

	public void setSettingControl(Runnable settingControl) {
		this.settingControl = settingControl;
	}
}
