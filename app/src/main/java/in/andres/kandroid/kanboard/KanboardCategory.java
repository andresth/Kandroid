package in.andres.kandroid.kanboard;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Thomas Andres on 11.01.17.
 */

public class KanboardCategory implements Serializable {
    public final int ID;
    public final String Name;
    public final int ProjectID;

    public KanboardCategory(JSONObject category) {
        ID = category.optInt("id");
        Name = category.optString("name");
        ProjectID = category.optInt("project_id");
    }
}
