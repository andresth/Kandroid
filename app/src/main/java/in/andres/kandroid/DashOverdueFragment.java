package in.andres.kandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Fragment to display  the list of users overdue tasks
 *
 * Created by Thomas Andres on 2017-01-07.
 */

public class DashOverdueFragment extends ListFragment {

    public DashOverdueFragment() {}

    public static DashOverdueFragment newInstance() {
        return new DashOverdueFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (((MainActivity)getActivity()).getDashboard() != null) {
            DashOverdueAdapter listAdapter = new DashOverdueAdapter(getActivity(), ((MainActivity)getActivity()).getDashboard().getOverdueTasks());
            setListAdapter(listAdapter);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        KanboardTask clickedTask = ((MainActivity)getActivity()).getDashboard().getOverdueTasks().get(position);
        Intent taskIntent = new Intent(getContext(), TaskDetailActivity.class);
        taskIntent.putExtra("task", clickedTask);
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
                convertView = mInflater.inflate(R.layout.listitem_dash_overdue, parent, false);

            TextView textName = (TextView) convertView.findViewById(R.id.task_name);
            TextView textOverdue = (TextView) convertView.findViewById(R.id.task_overdue);
            TextView textProject = (TextView) convertView.findViewById(R.id.task_project);

            textName.setText(mValues.get(position).getTitle());
            textProject.setText(mValues.get(position).getProjectName());
            long overdue = new Date().getTime() - mValues.get(position).getDateDue().getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(overdue);
            long days = TimeUnit.MILLISECONDS.toDays(overdue);

            if (days == 0)
                textOverdue.setText(Html.fromHtml(mContext.getResources().getQuantityString(R.plurals.format_overdue_hours, (int) hours, hours)));
            else
                textOverdue.setText(Html.fromHtml(mContext.getResources().getQuantityString(R.plurals.format_overdue_days, (int) days, days)));

            return convertView;
        }
    }
}
