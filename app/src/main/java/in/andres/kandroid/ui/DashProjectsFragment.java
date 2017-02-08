package in.andres.kandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import in.andres.kandroid.Constants;
import in.andres.kandroid.DashProjectsAdapter;
import in.andres.kandroid.R;
import in.andres.kandroid.kanboard.KanboardDashboard;
import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Fragment to display  the list of users projects and tasks
 *
 * Created by Thomas Andres on 2017-01-04.
 */

public class DashProjectsFragment extends Fragment {

    public DashProjectsFragment() {}

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Log.d("ProjectsFragment", "onCreateView");
        return inflater.inflate(R.layout.fragment_expandable_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (((MainActivity)getActivity()).getDashboard() != null) {
            assert getView() != null : "DashProjectsFragment: getView() returned null";
            getView().findViewById(R.id.fragment_dash_errortext).setVisibility(View.GONE);
            getView().findViewById(R.id.expandable_list).setVisibility(View.VISIBLE);
            DashProjectsAdapter listAdapter = new DashProjectsAdapter(getContext(), ((MainActivity)getActivity()).getDashboard());
            ((ExpandableListView) getView().findViewById(R.id.expandable_list)).setAdapter(listAdapter);
            ((ExpandableListView) getView().findViewById(R.id.expandable_list)).setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    Log.i(Constants.TAG, "Launching TaskDetailActivity from DashProjectsFragment.");
                    KanboardDashboard dashboard = ((MainActivity)getActivity()).getDashboard();
                    KanboardTask clickedTask = dashboard.getGroupedTasks().get(dashboard.getProjects().get(groupPosition).getId()).get(childPosition);
                    Intent taskIntent = new Intent(getContext(), TaskDetailActivity.class);
                    taskIntent.putExtra("task", clickedTask);
                    taskIntent.putExtra("me", ((MainActivity)getActivity()).getMe());
                    startActivity(taskIntent);
                    return true;
                }
            });
        } else {
            assert getView() != null : "DashProjectsFragment: getView() returned null";
            getView().findViewById(R.id.fragment_dash_errortext).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.expandable_list).setVisibility(View.GONE);
        }
    }
}
