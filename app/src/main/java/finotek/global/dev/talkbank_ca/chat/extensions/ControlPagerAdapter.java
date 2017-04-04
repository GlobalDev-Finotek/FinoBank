package finotek.global.dev.talkbank_ca.chat.extensions;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import finotek.global.dev.talkbank_ca.chat.MessageBox;

public class ControlPagerAdapter extends FragmentPagerAdapter {
    private final int NUM_PAGES = 2;
    private Runnable doOnControl;

    public ControlPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            ExtendedControl1 ec1 = new ExtendedControl1();
            ec1.setDoOnControl(doOnControl);
            return ec1;
        } else if(position == 1) {
            ExtendedControl2 ec2 = new ExtendedControl2();
            ec2.setDoOnControl(doOnControl);
            return ec2;
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    public void setDoOnControl(Runnable doOnControl) {
        this.doOnControl = doOnControl;
    }
}
