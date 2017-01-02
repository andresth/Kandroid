package in.andres.kandroid.kanboard;

import org.json.JSONObject;

/**
 * Created by Thomas Andres on 01.01.17.
 */

public class KanboardSubtask implements Comparable<KanboardSubtask> {
    public final int ID;
    public final int TaskID;
    public final int Position;
    public final int UserID;
    public final int Status;
    public final String Title;
    public final double TimeEstimated;
    public final double TimeSpent;
    // Dashboard properties
    public final int ProjectID;
    public final String TaskName;
    public final String ProjectName;
    public final String StatusName;
    public final boolean IsTimerStarted;
    public final int TimerStartDate;
    public final String ColorName;
    // TODO: What is timer_start_date?

    public KanboardSubtask(JSONObject json) {
        ID = json.optInt("id", -1);
        TaskID = json.optInt("task_id", -1);
        Position = json.optInt("position", -1);
        UserID = json.optInt("user_id", -1);
        Status = json.optInt("status");
        Title = json.optString("title");
        TimeEstimated = json.optDouble("time_estimated");
        TimeSpent = json.optDouble("time_spent");
        // Dashboard properties
        ProjectID = json.optInt("project_id", -1);
        TaskName = json.optString("task_name");
        ProjectName = json.optString("project_name");
        StatusName = json.optString("status_name");
        IsTimerStarted = KanboardAPI.StringToBoolean(json.optString("is_timer_started"));
        TimerStartDate = json.optInt("timer_start_date");
        ColorName = json.optString("color_id");
    }

    @Override
    public int compareTo(KanboardSubtask o) {
        return this.Title.compareTo(o.Title);
    }
}
