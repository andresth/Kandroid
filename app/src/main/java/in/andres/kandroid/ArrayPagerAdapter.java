/*
 * Copyright 2017 Thomas Andres
 *
 * This file is part of Kandroid.
 *
 * Kandroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kandroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package in.andres.kandroid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

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
        int idx = mFragments.indexOf((Fragment) object);
        if (idx == -1)
            return POSITION_NONE;
        else
            return idx;
    }
}
