package in.andres.kandroid.kanboard;

import org.json.JSONObject;

/**
 * Created by Thomas Andres on 01.01.17.
 */

public class KanboardColumn implements Comparable<KanboardColumn> {
    public final int ID;
    public final int Position;
    public final int TaskLimit;
    public final int NumberTasks;
    public final String Title;
    public final String Description;
    public final int ProjectID;
    public final boolean HideInDashboard;

    public KanboardColumn(JSONObject json) {
        ID = json.optInt("id");
        Position = json.optInt("position");
        TaskLimit = json.optInt("task_limit");
        NumberTasks = json.optInt("nb_tasks");
        Title = json.optString("title");
        Description = json.optString("description");
        ProjectID = json.optInt("project_id");
        HideInDashboard = KanboardAPI.StringToBoolean(json.optString("hide_in_dashboard"));
    }

    @Override
    public int compareTo(KanboardColumn o) {
        return this.Title.compareTo(o.Title);
    }
}
