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

package in.andres.kandroid.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import in.andres.kandroid.Utils;
import in.andres.kandroid.kanboard.KanboardActivity;

public class DashActivitiesFragment extends ListFragment {

    public DashActivitiesFragment() {}

    public static DashActivitiesFragment newInstance() {
        return new DashActivitiesFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (((MainActivity)getActivity()).getDashboard() != null) {
            DashActivityAdapter listAdapter = new DashActivityAdapter(getContext(), ((MainActivity)getActivity()).getDashboard().getActivities());
            setListAdapter(listAdapter);
        }
    }

    class DashActivityAdapter extends ArrayAdapter<KanboardActivity> {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<KanboardActivity> mValues;

        DashActivityAdapter(Context context, List<KanboardActivity> values) {
            super(context, android.R.layout.simple_list_item_1, values);
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mValues = values;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            textView.setText(Utils.fromHtml(mValues.get(position).getContent()));

            return convertView;
        }
    }
}
