package in.andres.kandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import in.andres.kandroid.kanboard.KanboardSubtask;
import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Created by Thomas Andres on 04.01.17.
 */

public class DashSubtasksFragment extends ListFragment {
    private List<KanboardSubtask> mSubtasks = null;

    public DashSubtasksFragment() {}

    public static DashSubtasksFragment newInstance() {
        return new DashSubtasksFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dash_projects, container, false);
        ArrayAdapter<KanboardSubtask> listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mSubtasks);
        setListAdapter(listAdapter);
        return rootView;
    }

    public void setSubtasks(List<KanboardSubtask> subtasks) {
        mSubtasks = subtasks;
    }
}
