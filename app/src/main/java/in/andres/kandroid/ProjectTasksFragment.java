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
 * Fragment to show tasks grouped by swimlane
 *
 * Created by Thomas Andres on 2017-01-04.
 */

public class ProjectTasksFragment extends Fragment {
    KanboardColumn mColumn;
    public ProjectTasksFragment() {}

    public static ProjectTasksFragment newInstance(KanboardColumn column) {
        ProjectTasksFragment fragment = new ProjectTasksFragment();
        Bundle args = new Bundle();
        args.putSerializable("column", column);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expandable_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (((MainActivity)getActivity()).getProject() != null) {
            assert getView() != null : "ProjectTaskFragment: getView() returned null";
            getView().findViewById(R.id.fragment_dash_errortext).setVisibility(View.GONE);
            getView().findViewById(R.id.expandable_list).setVisibility(View.VISIBLE);
            mColumn = (KanboardColumn) getArguments().getSerializable("column");
            ProjectTaskAdapter listAdapter = new ProjectTaskAdapter(getActivity(), ((MainActivity)getActivity()).getProject(), ((MainActivity)getActivity()).getProject().getGroupedActiveTasks().get(mColumn.getId()));
            ((ExpandableListView) getView().findViewById(R.id.expandable_list)).setAdapter(listAdapter);
            for (int i = 0; i < listAdapter.getGroupCount(); i++)
                ((ExpandableListView) getView().findViewById(R.id.expandable_list)).expandGroup(i);
            ((ExpandableListView) getView().findViewById(R.id.expandable_list)).setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    KanboardProject project = ((MainActivity) getActivity()).getProject();
                    KanboardTask clickedTask = project.getGroupedActiveTasks().get(mColumn.getId()).get(project.getSwimlanes().get(groupPosition).getId()).get(childPosition);
                    Intent taskIntent = new Intent(getContext(), TaskDetailActivity.class);
                    taskIntent.putExtra("task", clickedTask);
                    taskIntent.putExtra("me", ((MainActivity)getActivity()).getMe());
                    taskIntent.putExtra("column", mColumn);
                    taskIntent.putExtra("swimlane", project.getSwimlanes().get(groupPosition));
                    if (clickedTask.getCategoryId() > 0)
                        taskIntent.putExtra("category", project.getCategoryHashtable().get(clickedTask.getCategoryId()));
                    startActivity(taskIntent);
                    return true;
                }
            });
        } else {
            assert getView() != null : "ProjectTaskFragment: getView() returned null";
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
        assert getView() != null : "ProjectTaskFragment: getView() returned null";
        ExpandableListView lv = (ExpandableListView) getView().findViewById(R.id.expandable_list);
        for (int i = 0; i < lv.getExpandableListAdapter().getGroupCount(); i++)
            lv.expandGroup(i);
    }
}
