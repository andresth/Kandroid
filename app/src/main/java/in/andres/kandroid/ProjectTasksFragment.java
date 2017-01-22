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
//        Log.d("ProjectsFragment", "onCreateView");
        return inflater.inflate(R.layout.fragment_expandable_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Log.d("ProjectsFragment", "onActivityCreated");
        if (((MainActivity)getActivity()).getProject() != null) {
            getView().findViewById(R.id.fragment_dash_errortext).setVisibility(View.GONE);
            getView().findViewById(R.id.expandable_list).setVisibility(View.VISIBLE);
            mColumn = (KanboardColumn) getArguments().getSerializable("column");
            ProjectTaskAdapter listAdapter = new ProjectTaskAdapter(getActivity(), ((MainActivity)getActivity()).getProject(), mColumn);
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
                    taskIntent.putExtra("column", mColumn);
                    taskIntent.putExtra("swimlane", project.getSwimlanes().get(groupPosition));
                    if (clickedTask.getCategoryId() > 0)
                        taskIntent.putExtra("category", project.getCategoryHashtable().get(clickedTask.getCategoryId()));
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
//        outState.putSerializable("column", mColumn);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
//        mColumn = (KanboardColumn) savedInstanceState.getSerializable("column");
    }

    @Override
    public void onResume() {
        super.onResume();
        ExpandableListView lv = (ExpandableListView) getView().findViewById(R.id.expandable_list);
        for (int i = 0; i < lv.getExpandableListAdapter().getGroupCount(); i++)
            lv.expandGroup(i);
    }
}
