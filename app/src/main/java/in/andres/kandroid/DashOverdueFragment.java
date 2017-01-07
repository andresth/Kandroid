package in.andres.kandroid;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

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
            ArrayAdapter<KanboardTask> listAdapter = new ArrayAdapter<KanboardTask>(getActivity(), android.R.layout.simple_list_item_1, ((MainActivity)getActivity()).getDashboard().OverdueTasks);
            setListAdapter(listAdapter);
        }
    }
}
