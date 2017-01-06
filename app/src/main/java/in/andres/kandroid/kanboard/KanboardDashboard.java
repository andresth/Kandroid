package in.andres.kandroid.kanboard;

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

    public final String Json;

    public KanboardDashboard(JSONObject json) throws MalformedURLException {
        Json = json.toString();
        GroupedTasks = new Hashtable<>();
        Projects = new ArrayList<>();
        JSONArray projects = json.optJSONArray("projects");
        if (projects != null)
            for (int i = 0; i < projects.length(); i++) {
                KanboardProject tmpProject = new KanboardProject(projects.optJSONObject(i));
                Projects.add(tmpProject);
                GroupedTasks.put(tmpProject.ID, new ArrayList<KanboardTask>());
            }
        Tasks = new ArrayList<>();
        JSONArray tasks = json.optJSONArray("tasks");
        if (tasks != null)
            for (int i = 0; i < tasks.length(); i++) {
                KanboardTask tmpTask = new KanboardTask(tasks.optJSONObject(i));
                Tasks.add(tmpTask);
                GroupedTasks.get(tmpTask.ProjectID).add(tmpTask);
            }
        Subtasks = new ArrayList<>();
        JSONArray subtasks = json.optJSONArray("subtasks");
        if (subtasks != null)
            for (int i = 0; i < subtasks.length(); i++)
                Subtasks.add(new KanboardSubtask(subtasks.optJSONObject(i)));
    }
}
