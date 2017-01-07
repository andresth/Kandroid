package in.andres.kandroid;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import in.andres.kandroid.kanboard.KanboardActivity;
import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Created by Thomas Andres on 07.01.17.
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
            ArrayAdapter<KanboardActivity> listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, ((MainActivity)getActivity()).getDashboard().Activities);
            setListAdapter(listAdapter);
        }
    }
}
