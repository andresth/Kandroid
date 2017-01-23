package in.andres.kandroid.kanboard;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Thomas Andres on 11.01.17.
 */

public class KanboardSwimlane implements Serializable {
    private int Id;
    private String Name;
    private String Description;
    private int Position;
    private int ProjectId;
    private boolean IsActive;

    public KanboardSwimlane(JSONObject swimlane) {
        Id = swimlane.optInt("id");
        Name = swimlane.optString("name");
        Description = swimlane.optString("description");
        Position = swimlane.optInt("position");
        ProjectId = swimlane.optInt("project_id");
        IsActive = KanboardAPI.StringToBoolean(swimlane.optString("is_active"));
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public int getPosition() {
        return Position;
    }

    public int getProjectId() {
        return ProjectId;
    }

    public boolean getIsActive() {
        return IsActive;
    }

    @Override
    public String toString() {
        return this.Name;
    }
}
