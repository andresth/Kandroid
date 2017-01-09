package in.andres.kandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Created by Thomas Andres on 07.01.17.
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
            DashOverdueAdapter listAdapter = new DashOverdueAdapter(getActivity(), ((MainActivity)getActivity()).getDashboard().OverdueTasks);
            setListAdapter(listAdapter);
        }
    }

    class DashOverdueAdapter extends ArrayAdapter<KanboardTask> {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<KanboardTask> mValues;

        public DashOverdueAdapter(Context context, List<KanboardTask> values) {
            super(context, R.layout.listitem_dash_overdue, values);
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mValues = values;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.listitem_dash_overdue, null);

            TextView textName = (TextView) convertView.findViewById(R.id.task_name);
            TextView textOverdue = (TextView) convertView.findViewById(R.id.task_overdue);
            TextView textProject = (TextView) convertView.findViewById(R.id.task_project);

            textName.setText(mValues.get(position).Title);
            textProject.setText(mValues.get(position).ProjectName);
            long overdue = new Date().getTime() - mValues.get(position).DueDate.getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(overdue);
            long days = TimeUnit.MILLISECONDS.toDays(overdue);

            if (days == 0)
                textOverdue.setText(Html.fromHtml(String.format(Locale.getDefault(), getString(R.string.format_overdue_hours), hours)));
            else
                textOverdue.setText(Html.fromHtml(String.format(Locale.getDefault(), getString(R.string.format_overdue_days), days)));

            return convertView;
        }
    }
}
