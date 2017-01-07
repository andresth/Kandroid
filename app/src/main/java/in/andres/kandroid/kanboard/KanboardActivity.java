package in.andres.kandroid.kanboard;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Thomas Andres on 07.01.17.
 */

public class KanboardActivity {
    public final String Title;
    public final String Content;
    public final String Creator;
    public final String CreatorUserName;
    public final int CreatorID;
    public final int ID;
    public final int ProjectID;
    public final int TaskID;
    public final Date CreationDate;

    public KanboardActivity(JSONObject json) {
        Title = json.optString("event_title");
        Content = json.optString("event_content");
        Creator = json.optString("author");
        CreatorUserName = json.optString("author_username");
        CreatorID = json.optInt("creator_id");
        ID = json.optInt("id");
        ProjectID = json.optInt("project_id");
        TaskID = json.optInt("task_id");
        CreationDate = new Date(json.optLong("creation_date"));
    }
}
