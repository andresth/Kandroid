package in.andres.kandroid;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import in.andres.kandroid.kanboard.KanboardDashboard;

/**
 * Created by Thomas Andres on 03.01.17.
 */

public class DashPagerAdapter extends FragmentPagerAdapter {
    KanboardDashboard mDashboard;
    Context mContext;

    public DashPagerAdapter(FragmentManager fm, KanboardDashboard dash, Context context) {
        super(fm);
        mDashboard = dash;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DashProjectsFragment.newInstance();
            case 1:
                return DashTasksFragment.newInstance();
            case 2:
                return DashSubtasksFragment.newInstance();
        }
        return TextFragment.newInstance((String) this.getPageTitle(position));
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
                return mContext.getString(R.string.tab_tasks);
            case 2:
                return mContext.getString(R.string.tab_subtasks);
        }
        return  null;
    }
}
