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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import in.andres.kandroid.Constants;
import in.andres.kandroid.R;
import in.andres.kandroid.Utils;
import in.andres.kandroid.kanboard.KanboardTask;

public class DashOverdueFragment extends ListFragment {

    public DashOverdueFragment() {}

    public static DashOverdueFragment newInstance() {
        return new DashOverdueFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (((MainActivity)getActivity()).getDashboard() != null) {
            DashOverdueAdapter listAdapter = new DashOverdueAdapter(getContext(), ((MainActivity)getActivity()).getDashboard().getOverdueTasks());
            setListAdapter(listAdapter);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.i(Constants.TAG, "Launching TaskDetailActivity from DashOverdueFragment.");
        KanboardTask clickedTask = ((MainActivity)getActivity()).getDashboard().getOverdueTasks().get(position);
        Intent taskIntent = new Intent(getContext(), TaskDetailActivity.class);
        taskIntent.putExtra("task", clickedTask);
        taskIntent.putExtra("me", ((MainActivity)getActivity()).getMe());
        startActivity(taskIntent);
    }

    class DashOverdueAdapter extends ArrayAdapter<KanboardTask> {
        private Context mContext;
        private LayoutInflater mInflater;

        private List<KanboardTask> mValues;

        DashOverdueAdapter(Context context, List<KanboardTask> values) {
            super(context, R.layout.listitem_dash_overdue, values);
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mValues = values;
        }
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.listitem_project_task, parent, false);

            TextView textName = (TextView) convertView.findViewById(R.id.task_name);
            TextView textOverdue = (TextView) convertView.findViewById(R.id.task_owner);
            convertView.findViewById(R.id.task_category).setVisibility(View.INVISIBLE);

            textName.setText(Utils.fromHtml(String.format(Locale.getDefault(), "<big><b>%s</b></big><br />%s", mValues.get(position).getTitle(), mValues.get(position).getProjectName())));
            long overdue = new Date().getTime() - mValues.get(position).getDateDue().getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(overdue);
            long days = TimeUnit.MILLISECONDS.toDays(overdue);

            if (days == 0)
                textOverdue.setText(Utils.fromHtml(mContext.getResources().getQuantityString(R.plurals.format_overdue_hours, (int) hours, hours)));
            else
                textOverdue.setText(Utils.fromHtml(mContext.getResources().getQuantityString(R.plurals.format_overdue_days, (int) days, days)));

            if (mValues.get(position).getColorBackground() != null)
                convertView.findViewById(R.id.list_card).setBackgroundColor(mValues.get(position).getColorBackground());

            return convertView;
        }
    }
}
