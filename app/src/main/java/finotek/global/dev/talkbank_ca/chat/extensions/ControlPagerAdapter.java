package finotek.global.dev.talkbank_ca.chat.extensions;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ControlPagerAdapter extends FragmentPagerAdapter {
    private final int NUM_PAGES = 2;

    public ControlPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return new ExtendedControl1();
        } else if(position == 1) {
            return new ExtendedControl2();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
