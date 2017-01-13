package in.andres.kandroid;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import in.andres.kandroid.kanboard.KanboardDashboard;

/**
 * Created by Thomas Andres on 03.01.17.
 */

public class DashPagerAdapter extends FragmentPagerAdapter {
    private KanboardDashboard mDashboard;
    private Context mContext;
    private FragmentManager mFragmentManager;
    private List<Fragment> fragments;

    public DashPagerAdapter(FragmentManager fm, KanboardDashboard dash, Context context) {
        super(fm);
        mDashboard = dash;
        mContext = context;
        mFragmentManager = fm;
        fragments = new ArrayList<>();
    }

    public void clearAll() {
        for (Fragment fragment: fragments)
            mFragmentManager.beginTransaction().remove(fragment).commit();
        fragments.clear();
    }
    @Override
    public Fragment getItem(int position) {
        Fragment frag;
        switch (position) {
            case 0:
                frag = DashProjectsFragment.newInstance();
                fragments.add(frag);
                return frag;
            case 1:
                frag = DashOverdueFragment.newInstance();
                fragments.add(frag);
                return frag;
            case 2:
                frag = DashActivitiesFragment.newInstance();
                fragments.add(frag);
                return frag;
        }
        frag = TextFragment.newInstance((String) this.getPageTitle(position));
        fragments.add(frag);
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.tab_projects);
            case 1:
                return mContext.getString(R.string.tab_overdue_tasks);
            case 2:
                return mContext.getString(R.string.tab_activity);
        }
        return  null;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
