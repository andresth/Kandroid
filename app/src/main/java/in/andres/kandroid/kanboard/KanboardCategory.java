package in.andres.kandroid.kanboard;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Thomas Andres on 11.01.17.
 */

public class KanboardCategory implements Serializable {
    private int Id;
    private String Name;
    private int ProjectId;

    public KanboardCategory(JSONObject category) {
        Id = category.optInt("id");
        Name = category.optString("name");
        ProjectId = category.optInt("project_id");
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public int getProjectId() {
        return ProjectId;
    }

    @Override
    public String toString() {
        return this.Name;
    }
}
