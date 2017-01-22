package in.andres.kandroid.kanboard;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Thomas Andres on 01.01.17.
 */

public class KanboardColumn implements Comparable<KanboardColumn>, Serializable {
    private int Id;
    private int Position;
    private int TaskLimit;
    private int NumberTasks;
    private String Title;
    private String Description;
    private int ProjectId;
    private boolean HideInDashboard;

    public int getId() {
        return Id;
    }

    public int getPosition() {
        return Position;
    }

    public int getTaskLimit() {
        return TaskLimit;
    }

    public int getNumberTasks() {
        return NumberTasks;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public int getProjectId() {
        return ProjectId;
    }

    public boolean getHideInDashboard() {
        return HideInDashboard;
    }

    public KanboardColumn(JSONObject json) {
        Id = json.optInt("id");
        Position = json.optInt("position");
        TaskLimit = json.optInt("task_limit");
        NumberTasks = json.optInt("nb_tasks");
        Title = json.optString("title");
        Description = json.optString("description");
        ProjectId = json.optInt("project_id");
        HideInDashboard = KanboardAPI.StringToBoolean(json.optString("hide_in_dashboard"));
    }

    @Override
    public int compareTo(@Nullable KanboardColumn o) {
        return this.Title.compareTo(o.Title);
    }

    @Override
    public String toString() {
        return this.Title;
    }
}
