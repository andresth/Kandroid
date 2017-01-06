package in.andres.kandroid;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

import in.andres.kandroid.kanboard.KanboardProject;

/**
 * Created by Thomas Andres on 04.01.17.
 */

public class DashProjectsFragment extends ListFragment {
//    private List<KanboardProject> mProjects = null;
//
//    public static final String ARG_VALUES = "fragment_dashboard_projects";

    public DashProjectsFragment() {
//        setRetainInstance(true);
    }

    public static DashProjectsFragment newInstance() {
//    public static DashProjectsFragment newInstance(List<KanboardProject> values) {
        return new DashProjectsFragment();
        // FIXME: Fragment does not accept arguments
//        DashProjectsFragment fragment = new DashProjectsFragment();
//        fragment.setProjects(values);
//        Bundle args = new Bundle();
//        args.putSerializable(DashProjectsFragment.ARG_VALUES, (Serializable) values);
//        fragment.setArguments(args);
//        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.i("ProjectsFragment", Boolean.toString(getRetainInstance()));
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dash_projects, container, false);
//        DashProjectArrayAdapter listAdapter = new DashProjectArrayAdapter(getActivity(), mProjects);
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
            DashProjectArrayAdapter listAdapter = new DashProjectArrayAdapter(getActivity(), ((MainActivity)getActivity()).getDashboard().Projects);
            setListAdapter(listAdapter);
        } else {
            // TODO: show some kind of error
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        Log.d("ProjectsFragment", "Save instance");
//        savedInstanceState.putSerializable(DashProjectsFragment.ARG_VALUES, (Serializable) mProjects);
//        super.onSaveInstanceState(savedInstanceState);
//    }

//    public void setProjects(List<KanboardProject> projects) {
//        mProjects = projects;
//    }
}
