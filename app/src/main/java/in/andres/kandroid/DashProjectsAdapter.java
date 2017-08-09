/*
 * Copyright 2017 Thomas Andres
 *
 * This file is part of Kandroid.
 *
 * Kandroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kandroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package in.andres.kandroid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.commonmark.node.Paragraph;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.CoreHtmlNodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

import in.andres.kandroid.kanboard.KanboardColor;
import in.andres.kandroid.kanboard.KanboardColumn;
import in.andres.kandroid.kanboard.KanboardDashboard;
import in.andres.kandroid.kanboard.KanboardProject;
import in.andres.kandroid.kanboard.KanboardTask;

public class DashProjectsAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private KanboardDashboard mDashboard;
    private Dictionary<String, KanboardColor> mColors;
    private Parser mParser = Parser.builder().build();
    private HtmlRenderer mRenderer = HtmlRenderer.builder().nodeRendererFactory(new HtmlNodeRendererFactory() {
        @Override
        public NodeRenderer create(HtmlNodeRendererContext context) {
            // Compact paragraphs
            return new DashCompactHtmlRenderer(context);
        }
    }).build();

    public DashProjectsAdapter(@NonNull Context context, @NonNull KanboardDashboard values, @Nullable Dictionary<String, KanboardColor> colors) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDashboard = values;
        mColors = colors;
    }

    @Override
    public int getGroupCount() {
        return mDashboard.getProjects().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mDashboard.getGroupedTasks().get(mDashboard.getProjects().get(groupPosition).getId()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mDashboard.getProjects().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mDashboard.getGroupedTasks().get(mDashboard.getProjects().get(groupPosition).getId()).get(childPosition);
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
        // Aboard if no groups exist
        if (getGroupCount() == 0)
            return convertView;

        KanboardProject proj = (KanboardProject) getGroup(groupPosition);

        if (convertView == null)
            convertView = mInflater.inflate(R.layout.listitem_dash_project_header, parent, false);

        TextView projectName = (TextView) convertView.findViewById(R.id.project_name);
        TextView projectDescription = (TextView) convertView.findViewById(R.id.project_description);
        //FIXME: List item does not expand when links are clickable
//        projectDescription.setMovementMethod(LinkMovementMethod.getInstance());
        TextView projectColumns = (TextView) convertView.findViewById(R.id.project_columns);
        TextView projectNbTasks = (TextView) convertView.findViewById(R.id.project_nb_own_tasks);
        projectName.setText(proj.getName());
        if (proj.getDescription().contentEquals("") || proj.getDescription().contentEquals(""))
            projectDescription.setVisibility(View.GONE);
        else {
            projectDescription.setVisibility(View.VISIBLE);
            projectDescription.setText(Utils.fromHtml(mRenderer.render(mParser.parse(proj.getDescription()))));
        }
        List<String> columns = new ArrayList<>();
        for (KanboardColumn c: proj.getColumns())
            columns.add(String.format("<big><b>%1s</b></big> %2s", c.getNumberTasks(), c.getTitle()));
        projectColumns.setText(Utils.fromHtml(TextUtils.join(" <big><b>|</b></big> ", columns)));
        projectNbTasks.setText(mContext.getResources().getQuantityString(R.plurals.format_nb_tasks, getChildrenCount(groupPosition), getChildrenCount(groupPosition)));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        KanboardTask child = (KanboardTask) getChild(groupPosition, childPosition);

        if (convertView == null)
            convertView = mInflater.inflate(R.layout.listitem_project_task, parent, false);

        ((TextView) convertView.findViewById(R.id.task_name)).setText(Utils.fromHtml(String.format(Locale.getDefault(), "<big><b>#%d</b></big><br />%s", child.getId(), child.getTitle())));

        convertView.findViewById(R.id.task_owner).setVisibility(View.INVISIBLE);
        convertView.findViewById(R.id.task_category).setVisibility(View.INVISIBLE);

        if (mColors != null && child.getColorId() != null && !child.getColorId().isEmpty())
            convertView.findViewById(R.id.list_card).setBackgroundColor(mColors.get(child.getColorId()).getBackground());

//        if (child.getColorBackground() != null)
//            convertView.findViewById(R.id.list_card).setBackgroundColor(child.getColorBackground());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class DashCompactHtmlRenderer extends CoreHtmlNodeRenderer {

        private final HtmlWriter html;

        DashCompactHtmlRenderer(HtmlNodeRendererContext context) {
            super(context);
            this.html = context.getWriter();
        }

        @Override
        public void visit(Paragraph node) {
            // Replace paragraphs with line breaks to get a compact view.
            html.line();
            visitChildren(node);
            if (node.getNext() != null) {
                html.tag("br /");
                html.line();
            }
        }
    }
}
