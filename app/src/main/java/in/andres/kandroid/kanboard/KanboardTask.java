package in.andres.kandroid.kanboard;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Thomas Andres on 01.01.17.
 */

public class KanboardTask {
    public final int ID;
    public final int ProjectID;
    public final int ColumnID;
    public final int SwimlaneID;
    public final int CategoryID;
    public final int CreatorID;
    public final int Priority;
    public final boolean IsActive;
    public final String Title;
    public final String Description;
    public final URL Url;
    public final double TimeEstimated;
    public final double TimeSpent;
    // Dashboard properties
    public final String ProjectName;
    public final String ColumnTitle;
    // TODO: getTaskByID has additional properties
    // TODO: add color

    public KanboardTask(JSONObject json) throws MalformedURLException {
        ID = json.optInt("id", -1);
        ProjectID = json.optInt("project_id", -1);
        ColumnID = json.optInt("column_id", -1);
        SwimlaneID = json.optInt("swimlane_id", -1);
        CategoryID = json.optInt("category_id", -1);
        CreatorID = json.optInt("creator_id", -1);
        Priority = json.optInt("priority", -1);
        IsActive = KanboardAPI.StringToBoolean(json.optString("is_active"));
        Title = json.optString("title");
        Description = json.optString("description");
        Url = new URL(json.optString("url"));
        TimeEstimated = json.optDouble("time_estimated");
        TimeSpent = json.optDouble("time_spent");
        // Dashboard properties
        ProjectName = json.optString("project_name");
        ColumnTitle = json.optString("column_title");
    }
}
