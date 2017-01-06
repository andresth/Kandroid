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

import in.andres.kandroid.kanboard.KanboardSubtask;
import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Created by Thomas Andres on 04.01.17.
 */

public class DashSubtasksFragment extends ListFragment {
    private List<KanboardSubtask> mSubtasks = null;

    public static final String ARG_VALUES = "fragment_dashboard_subtasks";

    public DashSubtasksFragment() {
        setRetainInstance(true);
    }

    public static DashSubtasksFragment newInstance() {
        return new DashSubtasksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dash_projects, container, false);
//        if (savedInstanceState != null) {
//            Log.d("SubtasksFragment", "Load instance");
//            mSubtasks = (List<KanboardSubtask>) savedInstanceState.getSerializable(DashProjectsFragment.ARG_VALUES);
//        }
        ArrayAdapter<KanboardSubtask> listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mSubtasks);
        setListAdapter(listAdapter);
        return rootView;
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        Log.d("SubtasksFragment", "Save instance");
//        savedInstanceState.putSerializable(DashTasksFragment.ARG_VALUES, (Serializable) mSubtasks);
//        super.onSaveInstanceState(savedInstanceState);
//    }

    public void setSubtasks(List<KanboardSubtask> subtasks) {
        mSubtasks = subtasks;
    }
}
