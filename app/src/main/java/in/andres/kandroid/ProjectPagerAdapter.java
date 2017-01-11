package in.andres.kandroid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import in.andres.kandroid.kanboard.KanboardProject;

/**
 * Created by thomas on 11.01.17.
 */

public class ProjectPagerAdapter extends FragmentPagerAdapter {
    private KanboardProject mProject;

    public ProjectPagerAdapter(FragmentManager fm, KanboardProject project) {
        super(fm);
        mProject = project;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return TextFragment.newInstance(mProject.Description);
        else if ((position > 0) && (position < this.getCount()))
            return TextFragment.newInstance(mProject.Columns.get(position).Title);
        else if (position == this.getCount())
            return TextFragment.newInstance(Integer.toString(mProject.OverdueTasks.size()));
        else if (position == this.getCount() + 1)
            return TextFragment.newInstance(Integer.toString(mProject.InactiveTasks.size()));
        return TextFragment.newInstance((String) this.getPageTitle(position));
    }

    @Override
    public int getCount() {
        // +1 Overview | +1 Overdue | +1 Inactive
        return mProject.Columns.size() + 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Overview";
        } else if ((position > 0) && (position < this.getCount())) {
            return mProject.Columns.get(position - 1).Title;
        } else if (position == this.getCount()) {
            return "Overdue Tasks";
        } else if (position == this.getCount() + 1) {
            return "Inactive Tasks";
        }
        return null;
    }
}
