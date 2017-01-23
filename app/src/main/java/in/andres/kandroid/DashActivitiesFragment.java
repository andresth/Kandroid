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

import java.util.List;

import in.andres.kandroid.kanboard.KanboardActivity;

/**
 * Fragment to display  the list of users activities
 *
 * Created by Thomas Andres on 2017-01-07.
 */

public class DashActivitiesFragment extends ListFragment {

    public DashActivitiesFragment() {}

    public static DashActivitiesFragment newInstance() {
        return new DashActivitiesFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (((MainActivity)getActivity()).getDashboard() != null) {
            DashActivityAdapter listAdapter = new DashActivityAdapter(getActivity(), ((MainActivity)getActivity()).getDashboard().getActivities());
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
            textView.setText(Html.fromHtml(mValues.get(position).getContent()));

            return convertView;
        }
    }
}
