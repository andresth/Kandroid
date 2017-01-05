package in.andres.kandroid;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import in.andres.kandroid.kanboard.KanboardProject;

/**
 * Created by Thomas Andres on 04.01.17.
 */

public class DashProjectsFragment extends ListFragment {
    private List<KanboardProject> mProjects = null;

    public DashProjectsFragment() {}

    public static DashProjectsFragment newInstance() {
        return new DashProjectsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dash_projects, container, false);
        DashProjectArrayAdapter listAdapter = new DashProjectArrayAdapter(getActivity(), mProjects);
//        ArrayAdapter<KanboardProject> listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mProjects);
        setListAdapter(listAdapter);
        return rootView;
    }

    public void setProjects(List<KanboardProject> projects) {
        mProjects = projects;
    }
}
