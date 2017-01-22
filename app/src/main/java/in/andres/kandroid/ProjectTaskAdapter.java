package in.andres.kandroid;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

import in.andres.kandroid.kanboard.KanboardDashboard;
import in.andres.kandroid.kanboard.KanboardProject;
import in.andres.kandroid.kanboard.KanboardSwimlane;
import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Created by Thomas Andres on 06.01.17.
 */

public class ProjectTaskAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private KanboardDashboard mDashboard;
    private KanboardProject mProject;
    private Dictionary<Integer, List<KanboardTask>> mData;

    public ProjectTaskAdapter(Context context, KanboardDashboard values) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDashboard = values;
        mProject = null;
    }

    public ProjectTaskAdapter(Context context, KanboardProject values, Dictionary<Integer, List<KanboardTask>> data) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDashboard = null;
        mProject = values;
        mData = data;
    }

    @Override
    public int getGroupCount() {
        return mProject.getSwimlanes().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(mProject.getSwimlanes().get(groupPosition).getId()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mProject.getSwimlanes().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mData.get(mProject.getSwimlanes().get(groupPosition).getId()).get(childPosition);
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
        int taskCount = mData.get(swimlane.getId()).size();
        projectNbTasks.setText(mContext.getResources().getQuantityString(R.plurals.format_nb_tasks, taskCount, taskCount));
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) sidebar.getLayoutParams();
        lp.removeRule(RelativeLayout.ALIGN_BOTTOM);
//        if (!swimlane.Description.contentEquals("") && swimlane.Description != null) {
            projectDescription.setText(swimlane.getDescription());
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
        final KanboardTask child = (KanboardTask) getChild(groupPosition, childPosition);

        if (convertView == null)
            convertView = mInflater.inflate(R.layout.listitem_project_task, null);

        ((TextView) convertView.findViewById(R.id.task_name)).setText(Html.fromHtml(String.format(Locale.getDefault(), "<big><b>#%d</b></big><br />%s", child.getId(), child.getTitle())));

        if (mProject.getProjectUsers().get(child.getOwnerId()) != null)
            ((TextView) convertView.findViewById(R.id.task_owner)).setText(Html.fromHtml(String.format(Locale.getDefault(), "<small>%s</small>", mProject.getProjectUsers().get(child.getOwnerId()))));
        else
            convertView.findViewById(R.id.task_owner).setVisibility(View.INVISIBLE);

        if (child.getColorBackground() != null)
            convertView.findViewById(R.id.list_card).setBackgroundColor(child.getColorBackground());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
