package in.andres.kandroid.kanboard;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Thomas Andres on 01.01.17.
 */

public class KanboardSubtask implements Comparable<KanboardSubtask>, Serializable {
    private int Id;
    private int TaskId;
    private int Position;
    private int UserId;
    private int Status;
    private String Title;
    private double TimeEstimated;
    private double TimeSpent;
    // Dashboard properties
    private int ProjectId;
    private String TaskName;
    private String ProjectName;
    private String StatusName;
    private boolean IsTimerStarted;
    private int TimerStartDate;
    private String ColorName;
    // TODO: What is timer_start_date?

    public KanboardSubtask(JSONObject json) {
        Id = json.optInt("id", -1);
        TaskId = json.optInt("task_id", -1);
        Position = json.optInt("position", -1);
        UserId = json.optInt("user_id", -1);
        Status = json.optInt("status");
        Title = json.optString("title");
        TimeEstimated = Double.parseDouble(json.optString("time_estimated"));
        TimeSpent = Double.parseDouble(json.optString("time_spent"));
        // Dashboard properties
        ProjectId = json.optInt("project_id", -1);
        TaskName = json.optString("task_name");
        ProjectName = json.optString("project_name");
        StatusName = json.optString("status_name");
        IsTimerStarted = KanboardAPI.StringToBoolean(json.optString("is_timer_started"));
        TimerStartDate = json.optInt("timer_start_date");
        ColorName = json.optString("color_id");
    }

    public int getId() {
        return Id;
    }

    public int getTaskId() {
        return TaskId;
    }

    public int getPosition() {
        return Position;
    }

    public int getUserId() {
        return UserId;
    }

    public int getStatus() {
        return Status;
    }

    public String getTitle() {
        return Title;
    }

    public double getTimeEstimated() {
        return TimeEstimated;
    }

    public double getTimeSpent() {
        return TimeSpent;
    }

    public int getProjectId() {
        return ProjectId;
    }

    public String getTaskName() {
        return TaskName;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public String getStatusName() {
        return StatusName;
    }

    public boolean getIsTimerStarted() {
        return IsTimerStarted;
    }

    public int getTimerStartDate() {
        return TimerStartDate;
    }

    public String getColorName() {
        return ColorName;
    }

    @Override
    public int compareTo(@NonNull KanboardSubtask o) {
        return this.Title.compareTo(o.Title);
    }

    @Override
    public String toString() {
        return this.Title;
    }
}
