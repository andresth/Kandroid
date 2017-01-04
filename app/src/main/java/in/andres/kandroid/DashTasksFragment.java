package in.andres.kandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import in.andres.kandroid.kanboard.KanboardProject;
import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Created by Thomas Andres on 04.01.17.
 */

public class DashTasksFragment extends ListFragment {
    private List<KanboardTask> mTasks = null;

    public DashTasksFragment() {}

    public static DashTasksFragment newInstance() {
        return new DashTasksFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dash_projects, container, false);
        ArrayAdapter<KanboardTask> listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mTasks);
        setListAdapter(listAdapter);
        return rootView;
    }

    public void setTasks(List<KanboardTask> tasks) {
        mTasks = tasks;
    }
}
