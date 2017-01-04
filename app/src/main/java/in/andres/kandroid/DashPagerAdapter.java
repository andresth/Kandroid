package in.andres.kandroid;

import android.content.Context;
import android.content.res.Resources;
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
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = DashProjectsFragment.newInstance();
                ((DashProjectsFragment)fragment).setProjects(mDashboard.Projects);
                return fragment;
            case 1:
                fragment = DashTasksFragment.newInstance();
                ((DashTasksFragment)fragment).setTasks(mDashboard.Tasks);
                return fragment;
            case 2:
                fragment = DashSubtasksFragment.newInstance();
                ((DashSubtasksFragment)fragment).setSubtasks(mDashboard.Subtasks);
                return fragment;
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
