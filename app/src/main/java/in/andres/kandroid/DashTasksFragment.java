package in.andres.kandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.List;

import in.andres.kandroid.kanboard.KanboardProject;
import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Created by Thomas Andres on 04.01.17.
 */

public class DashTasksFragment extends ListFragment {
//    private List<KanboardTask> mTasks = null;
//
//    public static final String ARG_VALUES = "fragment_dashboard_tasks";

    public DashTasksFragment() {}

    public static DashTasksFragment newInstance() {
        return new DashTasksFragment();
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dash_projects, container, false);
//        if (savedInstanceState != null) {
//            Log.d("TasksFragment", "Load instance");
//            mTasks = (List<KanboardTask>) savedInstanceState.getSerializable(DashProjectsFragment.ARG_VALUES);
//            Log.d("TasksFragment", Integer.toString(mTasks.size()));
//        }
//        ArrayAdapter<KanboardTask> listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mTasks);
//        setListAdapter(listAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (savedInstanceState != null) {
//            Log.d("ProjectsFragment", "Load instance");
//            mProjects = (List<KanboardProject>) savedInstanceState.getSerializable(DashProjectsFragment.ARG_VALUES);
//            Log.d("ProjectsFragment", Integer.toString(mProjects.size()));
//        } else {
//            Log.d("ProjectsFragment", "No instance");
//        }
        if (((MainActivity)getActivity()).getDashboard() != null) {
            ArrayAdapter<KanboardTask> listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, ((MainActivity)getActivity()).getDashboard().Tasks);
            setListAdapter(listAdapter);
        } else {
            // TODO: show some kind of error
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        Log.d("TasksFragment", "Save instance");
//        savedInstanceState.putSerializable(DashTasksFragment.ARG_VALUES, (Serializable) mTasks);
//        super.onSaveInstanceState(savedInstanceState);
//    }

//    public void setTasks(List<KanboardTask> tasks) {
//        mTasks = tasks;
//    }
}
