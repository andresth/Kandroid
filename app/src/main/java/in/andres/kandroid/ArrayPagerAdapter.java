package in.andres.kandroid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Andres on 13.01.17.
 */

public class ArrayPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;
    private List<String> mPageTitle;
    private FragmentManager mFragmentManager;

    public ArrayPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
        mPageTitle = new ArrayList<>();
        mFragmentManager = fm;
    }

    public int addFragment(Fragment fragment, String title) {
        return addFragment(fragment, title, mFragments.size());
    }

    public int addFragment(Fragment fragment,String title, int position) {
        mFragments.add(position, fragment);
        mPageTitle.add(position, title);
        return position;
    }

    public void removeAllFragments() {
        for (Fragment fragment: mFragments)
            mFragmentManager.beginTransaction().remove(fragment).commit();
        mFragments.clear();
        mPageTitle.clear();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitle.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        int idx = mFragments.indexOf(object);
        if (idx == -1)
            return POSITION_NONE;
        else
            return idx;
    }
}
