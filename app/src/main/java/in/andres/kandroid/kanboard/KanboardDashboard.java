package in.andres.kandroid.kanboard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Thomas Andres on 01.01.17.
 */

public class KanboardDashboard implements Serializable {
    public final List<KanboardProject> Projects;
    public final List<KanboardTask> Tasks;
    public final List<KanboardSubtask> Subtasks;
    public final Dictionary<Integer, List<KanboardTask>> GroupedTasks;
    public final List<KanboardTask> OverdueTasks;
    public final List<KanboardActivity> Activities;

    public final String Json;

    public KanboardDashboard(@NonNull JSONObject dashboard) throws MalformedURLException {
        this(dashboard, null, null);
    }

    public KanboardDashboard(@NonNull JSONObject dashboard, @Nullable JSONArray overdue, @Nullable JSONArray activities) throws MalformedURLException {

        Json = dashboard.toString();
        GroupedTasks = new Hashtable<>();
        Projects = new ArrayList<>();
        JSONArray projects = dashboard.optJSONArray("projects");
        if (projects != null)
            for (int i = 0; i < projects.length(); i++) {
                KanboardProject tmpProject = new KanboardProject(projects.optJSONObject(i));
                Projects.add(tmpProject);
                GroupedTasks.put(tmpProject.ID, new ArrayList<KanboardTask>());
            }
        Tasks = new ArrayList<>();
        JSONArray tasks = dashboard.optJSONArray("tasks");
        if (tasks != null)
            for (int i = 0; i < tasks.length(); i++) {
                KanboardTask tmpTask = new KanboardTask(tasks.optJSONObject(i));
                Tasks.add(tmpTask);
                GroupedTasks.get(tmpTask.getProjectId()).add(tmpTask);
            }
        Subtasks = new ArrayList<>();
        JSONArray subtasks = dashboard.optJSONArray("subtasks");
        if (subtasks != null)
            for (int i = 0; i < subtasks.length(); i++)
                Subtasks.add(new KanboardSubtask(subtasks.optJSONObject(i)));

        if (overdue != null) {
            OverdueTasks = new ArrayList<>();
            for (int i = 0; i < overdue.length(); i++) {
                OverdueTasks.add(new KanboardTask(overdue.optJSONObject(i)));
            }
        } else {
            OverdueTasks = null;
        }

        if (activities != null) {
            Activities = new ArrayList<>();
            for (int i = 0; i < activities.length(); i++) {
                Activities.add(new KanboardActivity(activities.optJSONObject(i)));
            }
        } else {
            Activities = null;
        }
    }
}
