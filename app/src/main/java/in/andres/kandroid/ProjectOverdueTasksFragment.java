package in.andres.kandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import in.andres.kandroid.kanboard.KanboardColumn;
import in.andres.kandroid.kanboard.KanboardProject;
import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Created by Thomas Andres on 04.01.17.
 */

public class ProjectOverdueTasksFragment extends Fragment {
    KanboardColumn mColumn;
    public ProjectOverdueTasksFragment() {}

    public static ProjectOverdueTasksFragment newInstance() {
        ProjectOverdueTasksFragment fragment = new ProjectOverdueTasksFragment();
        return fragment;
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
        if (((MainActivity)getActivity()).getProject() != null) {
            getView().findViewById(R.id.fragment_dash_errortext).setVisibility(View.GONE);
            getView().findViewById(R.id.expandable_list).setVisibility(View.VISIBLE);
            ProjectTaskAdapter listAdapter = new ProjectTaskAdapter(getActivity(), ((MainActivity)getActivity()).getProject(), ((MainActivity)getActivity()).getProject().getGroupedOverdueTasks());
            ((ExpandableListView) getView().findViewById(R.id.expandable_list)).setAdapter(listAdapter);
            for (int i = 0; i < listAdapter.getGroupCount(); i++)
                ((ExpandableListView) getView().findViewById(R.id.expandable_list)).expandGroup(i);
            ((ExpandableListView) getView().findViewById(R.id.expandable_list)).setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    KanboardProject project = ((MainActivity) getActivity()).getProject();
                    KanboardTask clickedTask = project.getGroupedOverdueTasks().get(project.getSwimlanes().get(groupPosition).getId()).get(childPosition);
                    Intent taskIntent = new Intent(getContext(), TaskDetailActivity.class);
                    taskIntent.putExtra("task", clickedTask);
                    startActivity(taskIntent);
                    return true;
                }
            });
        } else {
            getView().findViewById(R.id.fragment_dash_errortext).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.expandable_list).setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ExpandableListView lv = (ExpandableListView) getView().findViewById(R.id.expandable_list);
        for (int i = 0; i < lv.getExpandableListAdapter().getGroupCount(); i++)
            lv.expandGroup(i);
    }
}
