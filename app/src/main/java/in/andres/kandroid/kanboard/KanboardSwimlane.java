package in.andres.kandroid.kanboard;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Thomas Andres on 11.01.17.
 */

public class KanboardSwimlane implements Serializable {
    public final int ID;
    public final String Name;
    public final int Position;
    public final int ProjectID;
    public final boolean IsActive;

    public KanboardSwimlane(JSONObject swimlane) {
        ID = swimlane.optInt("id");
        Name = swimlane.optString("name");
        Position = swimlane.optInt("position");
        ProjectID = swimlane.optInt("project_id");
        IsActive = KanboardAPI.StringToBoolean(swimlane.optString("is_active"));
    }
}
