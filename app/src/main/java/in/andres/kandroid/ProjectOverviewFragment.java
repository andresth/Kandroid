package in.andres.kandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.text.format.DateFormat;

import in.andres.kandroid.kanboard.KanboardProject;

/**
 * Created by Thomas Andres on 15.01.17.
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
            TextView projectName = (TextView) getView().findViewById(R.id.project_name);
            TextView projectDescription = (TextView) getView().findViewById(R.id.project_description);
            TextView projectNBActiveTasks = (TextView) getView().findViewById(R.id.project_active_tasks);
            TextView projectNBInactiveTasks = (TextView) getView().findViewById(R.id.project_inactive_tasks);
            TextView projectNBOverdueTasks = (TextView) getView().findViewById(R.id.project_overdue_tasks);
            TextView projectNBTotalTasks = (TextView) getView().findViewById(R.id.project_total_tasks);
            TextView projectModifyDate = (TextView) getView().findViewById(R.id.project_modify_date);

            projectName.setText(project.Name);
            projectDescription.setText(project.Description);
            projectNBActiveTasks.setText(getContext().getResources().getQuantityString(R.plurals.format_nb_active_tasks, project.ActiveTasks.size(), project.ActiveTasks.size()));
            projectNBInactiveTasks.setText(getContext().getResources().getQuantityString(R.plurals.format_nb_inactive_tasks, project.InactiveTasks.size(), project.InactiveTasks.size()));
            projectNBOverdueTasks.setText(getContext().getResources().getQuantityString(R.plurals.format_nb_overdue_tasks, project.OverdueTasks.size(), project.OverdueTasks.size()));
            projectNBTotalTasks.setText(getContext().getResources().getQuantityString(R.plurals.format_nb_total_tasks, project.ActiveTasks.size() + project.InactiveTasks.size(), project.ActiveTasks.size() + project.InactiveTasks.size()));
            projectModifyDate.setText(DateFormat.getLongDateFormat(getContext()).format(project.LastModified) + " " + DateFormat.getTimeFormat(getContext()).format(project.LastModified));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_overview, container, false);
    }
}
