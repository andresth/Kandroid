package in.andres.kandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.andres.kandroid.kanboard.KanboardColumn;
import in.andres.kandroid.kanboard.KanboardProject;
import in.andres.kandroid.kanboard.KanboardSwimlane;
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
            ProjectOverdueTaskAdapter listAdapter = new ProjectOverdueTaskAdapter(getActivity(), ((MainActivity)getActivity()).getProject());
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

    class ProjectOverdueTaskAdapter extends BaseExpandableListAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private KanboardProject mProject;

        public ProjectOverdueTaskAdapter(Context context, KanboardProject values) {
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mProject = values;
        }

        @Override
        public int getGroupCount() {
            return mProject.getSwimlanes().size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mProject.getGroupedOverdueTasks().get(mProject.getSwimlanes().get(groupPosition).getId()).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mProject.getSwimlanes().get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mProject.getGroupedOverdueTasks().get(mProject.getSwimlanes().get(groupPosition).getId()).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            KanboardSwimlane swimlane = (KanboardSwimlane) getGroup(groupPosition);

            if (convertView == null)
                convertView = mInflater.inflate(R.layout.listitem_dash_project_header, null);

            TextView projectName = (TextView) convertView.findViewById(R.id.project_name);
            TextView projectDescription = (TextView) convertView.findViewById(R.id.project_description);
            TextView projectColumns = (TextView) convertView.findViewById(R.id.project_columns);
            TextView projectNbTasks = (TextView) convertView.findViewById(R.id.project_nb_own_tasks);
            TextView sidebar = (TextView) convertView.findViewById(R.id.sidebar);

            projectName.setText(swimlane.getName());
            int taskCount = mProject.getGroupedOverdueTasks().get(swimlane.getId()).size();
            projectNbTasks.setText(mContext.getResources().getQuantityString(R.plurals.format_nb_tasks, taskCount, taskCount));
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) sidebar.getLayoutParams();
            lp.removeRule(RelativeLayout.ALIGN_BOTTOM);
            projectDescription.setText(swimlane.getDescription());
            lp.addRule(RelativeLayout.ALIGN_BOTTOM, projectDescription.getId());
            sidebar.setLayoutParams(lp);
            projectColumns.setVisibility(View.GONE);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            String childTitle = ((KanboardTask) getChild(groupPosition, childPosition)).getTitle();

            if (convertView == null)
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);

            TextView text = (TextView) convertView.findViewById(android.R.id.text1);
            text.setText(childTitle);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
