package in.andres.kandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import in.andres.kandroid.kanboard.KanboardColumn;
import in.andres.kandroid.kanboard.KanboardDashboard;
import in.andres.kandroid.kanboard.KanboardProject;
import in.andres.kandroid.kanboard.KanboardSwimlane;
import in.andres.kandroid.kanboard.KanboardTask;

;

/**
 * Created by Thomas Andres on 06.01.17.
 */

public class ProjectTaskAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private KanboardDashboard mDashboard;
    private KanboardProject mProject;
    private KanboardColumn mColumn;

    public ProjectTaskAdapter(Context context, KanboardDashboard values) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDashboard = values;
        mProject = null;
    }

    public ProjectTaskAdapter(Context context, KanboardProject values, KanboardColumn column) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDashboard = null;
        mProject = values;
        mColumn = column;
    }

    @Override
    public int getGroupCount() {
        return mProject.Swimlanes.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mProject.GroupedActiveTasks.get(mColumn.ID).get(mProject.Swimlanes.get(groupPosition).ID).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mProject.Swimlanes.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mProject.GroupedActiveTasks.get(mColumn.ID).get(mProject.Swimlanes.get(groupPosition).ID).get(childPosition);
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

        projectName.setText(swimlane.Name);
        projectNbTasks.setText(String.format(Locale.getDefault(), mContext.getString(R.string.format_nb_tasks),
                mProject.GroupedActiveTasks.get(mColumn.ID).get(swimlane.ID).size()));
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) sidebar.getLayoutParams();
        lp.removeRule(RelativeLayout.ALIGN_BOTTOM);
//        if (!swimlane.Description.contentEquals("") && swimlane.Description != null) {
            projectDescription.setText(swimlane.Description);
            lp.addRule(RelativeLayout.ALIGN_BOTTOM, projectDescription.getId());
//        } else {
//            projectDescription.setVisibility(View.GONE);
//            lp.addRule(RelativeLayout.ALIGN_BOTTOM, projectName.getId());
//        }
        sidebar.setLayoutParams(lp);
        projectColumns.setVisibility(View.GONE);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childTitle = ((KanboardTask) getChild(groupPosition, childPosition)).Title;

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
