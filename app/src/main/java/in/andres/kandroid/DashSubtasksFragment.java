package in.andres.kandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import in.andres.kandroid.kanboard.KanboardSubtask;

/**
 * Created by Thomas Andres on 04.01.17.
 */

public class DashSubtasksFragment extends ListFragment {

    public DashSubtasksFragment() {}

    public static DashSubtasksFragment newInstance() {
        return new DashSubtasksFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dash_projects, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (((MainActivity)getActivity()).getDashboard() != null) {
            getView().findViewById(R.id.fragment_dash_errortext).setVisibility(View.GONE);
            getView().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
            ArrayAdapter<KanboardSubtask> listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, ((MainActivity)getActivity()).getDashboard().Subtasks);
            setListAdapter(listAdapter);
        } else {
            getView().findViewById(R.id.fragment_dash_errortext).setVisibility(View.VISIBLE);
            getView().findViewById(android.R.id.list).setVisibility(View.GONE);
        }
    }
}
