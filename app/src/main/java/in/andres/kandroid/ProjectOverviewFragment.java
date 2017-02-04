package in.andres.kandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;

import in.andres.kandroid.kanboard.KanboardProject;
import us.feras.mdv.MarkdownView;

/**
 * Fragment to show general information aboout a project
 *
 * Created by Thomas Andres on 2017-01-15.
 */

public class ProjectOverviewFragment extends Fragment {

    public ProjectOverviewFragment() {}

    public static Fragment newInstance() {
        return new ProjectOverviewFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        KanboardProject project = ((MainActivity) getActivity()).getProject();
        if (project != null) {
            assert getView() != null : "ProjectOverviewFragment: getView() returned null";
            MarkdownView projectDescription = (MarkdownView) getView().findViewById(R.id.project_description);
            TextView projectNBActiveTasks = (TextView) getView().findViewById(R.id.project_active_tasks);
            TextView projectNBInactiveTasks = (TextView) getView().findViewById(R.id.project_inactive_tasks);
            TextView projectNBOverdueTasks = (TextView) getView().findViewById(R.id.project_overdue_tasks);
            TextView projectNBTotalTasks = (TextView) getView().findViewById(R.id.project_total_tasks);
            TextView projectModifyDate = (TextView) getView().findViewById(R.id.project_modify_date);
            TextView projectMembers = (TextView) getView().findViewById(R.id.project_members);
            TextView projectColumns = (TextView) getView().findViewById(R.id.project_columns);
            TextView projectSwimlanes = (TextView) getView().findViewById(R.id.project_swimlanes);

            if (project.getDescription() != null)
                projectDescription.loadMarkdown(project.getDescription());
            else
                getView().findViewById(R.id.card_description).setVisibility(View.GONE);
            projectMembers.setText(Html.fromHtml(TextUtils.join(" <big><b>|</b></big> ", Collections.list(project.getProjectUsers().elements()))));
            projectColumns.setText(Html.fromHtml(TextUtils.join(" <big><b>|</b></big> ", project.getColumns())));
            projectSwimlanes.setText(Html.fromHtml(TextUtils.join(" <big><b>|</b></big> ", project.getSwimlanes())));
            projectNBActiveTasks.setText(getContext().getResources().getQuantityString(R.plurals.format_nb_active_tasks, project.getActiveTasks().size(), project.getActiveTasks().size()));
            projectNBInactiveTasks.setText(getContext().getResources().getQuantityString(R.plurals.format_nb_inactive_tasks, project.getInactiveTasks().size(), project.getInactiveTasks().size()));
            projectNBOverdueTasks.setText(getContext().getResources().getQuantityString(R.plurals.format_nb_overdue_tasks, project.getOverdueTasks().size(), project.getOverdueTasks().size()));
            projectNBTotalTasks.setText(getContext().getResources().getQuantityString(R.plurals.format_nb_total_tasks, project.getActiveTasks().size() + project.getInactiveTasks().size(), project.getActiveTasks().size() + project.getInactiveTasks().size()));
            projectModifyDate.setText(DateFormat.getLongDateFormat(getContext()).format(project.getLastModified()) + " " + DateFormat.getTimeFormat(getContext()).format(project.getLastModified()));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_overview, container, false);
    }
}
