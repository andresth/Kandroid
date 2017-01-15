package in.andres.kandroid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import in.andres.kandroid.kanboard.KanboardProject;

/**
 * Created by thomas on 11.01.17.
 */

//TODO: Obsolete, remove

public class ProjectPagerAdapter extends FragmentPagerAdapter {
    private KanboardProject mProject;
    private FragmentManager mFragmentManager;
    private List<Fragment> fragments;

    public ProjectPagerAdapter(FragmentManager fm, KanboardProject project) {
        super(fm);
        mProject = project;
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
        if (position == 0) {
            frag = TextFragment.newInstance(mProject.Description);
            fragments.add(frag);
            return frag;
        } else if ((position > 0) && (position <= mProject.Columns.size())) {
            frag = TextFragment.newInstance(mProject.Columns.get(position - 1).Title);
            fragments.add(frag);
            return frag;
        } else if (position == this.getCount() - 2) {
            frag = TextFragment.newInstance(Integer.toString(mProject.OverdueTasks.size()));
            fragments.add(frag);
            return frag;
        } else if (position == this.getCount() - 1) {
            frag = TextFragment.newInstance(Integer.toString(mProject.InactiveTasks.size()));
            fragments.add(frag);
            return frag;
        }
        frag = TextFragment.newInstance((String) this.getPageTitle(position));
        fragments.add(frag);
        return frag;
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
        } else if ((position > 0) && (position <= mProject.Columns.size())) {
            return mProject.Columns.get(position - 1).Title;
        } else if (position == this.getCount() - 2) {
            return "Overdue Tasks";
        } else if (position == this.getCount() -1 ) {
            return "Inactive Tasks";
        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
