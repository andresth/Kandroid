package in.andres.kandroid;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;;import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.zip.Inflater;

import in.andres.kandroid.kanboard.KanboardColumn;
import in.andres.kandroid.kanboard.KanboardDashboard;
import in.andres.kandroid.kanboard.KanboardProject;
import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Created by Thomas Andres on 06.01.17.
 */

public class DashProjectsAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private KanboardDashboard mDashboard;
    private KanboardProject mProject;

    public DashProjectsAdapter(Context context, KanboardDashboard values) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDashboard = values;
        mProject = null;
    }

    public DashProjectsAdapter(Context context, KanboardProject values) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDashboard = null;
        mProject = values;
    }

    @Override
    public int getGroupCount() {
        if (mDashboard != null)
          return mDashboard.GroupedTasks.size();
        // TODO: Add Swimlanes

        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mDashboard != null)
            return mDashboard.GroupedTasks.get(mDashboard.Projects.get(groupPosition).ID).size();
        // TODO: Add Swimlanes

        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (mDashboard != null)
            return mDashboard.Projects.get(groupPosition);
        // TODO: Add Swimlanes

        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (mDashboard != null)
            return mDashboard.GroupedTasks.get(mDashboard.Projects.get(groupPosition).ID).get(childPosition);
        // TODO: Add Swimlanes

        return null;
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
        String groupTitle = ((KanboardProject) getGroup(groupPosition)).Name;
        KanboardProject proj = (KanboardProject) getGroup(groupPosition);

        if (convertView == null)
            convertView = mInflater.inflate(R.layout.listitem_dash_project_header, null);
//            convertView = mInflater.inflate(R.layout.listitem_task_group, null);


//        TextView projectID = (TextView) convertView.findViewById(R.id.project_id);
        TextView projectName = (TextView) convertView.findViewById(R.id.project_name);
        TextView projectDescription = (TextView) convertView.findViewById(R.id.project_description);
        TextView projectColumns = (TextView) convertView.findViewById(R.id.project_columns);
//        ImageView projectPrivate = (ImageView) convertView.findViewById(R.id.project_private);
        TextView projectNbTasks = (TextView) convertView.findViewById(R.id.project_nb_own_tasks);

//        projectID.setText(Html.fromHtml(String.format("#<b>%1s</b>", Integer.toString(proj.ID))));
        projectName.setText(proj.Name);
//        projectPrivate.setImageDrawable(mContext.getDrawable(proj.IsPrivate ? R.drawable.project_private : R.drawable.project_public));
        if ((proj.Description == null) || proj.Description.contentEquals(""))
            projectDescription.setVisibility(View.GONE);
        projectDescription.setText(proj.Description);
        List<String> columns = new ArrayList<>();
        for (KanboardColumn c: proj.Columns)
            columns.add(String.format("<big><b>%1s</b></big> %2s", c.NumberTasks, c.Title));
        projectColumns.setText(Html.fromHtml(TextUtils.join(" <big><b>|</b></big> ", columns)));
        projectNbTasks.setText(String.format(Locale.getDefault(), mContext.getString(R.string.format_nb_tasks), getChildrenCount(groupPosition)));

//        TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
//        text1.setText(groupTitle);
//
//        TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
//        text2.setText(String.format(Locale.getDefault(), mContext.getString(R.string.format_nb_tasks), getChildrenCount(groupPosition)));

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
