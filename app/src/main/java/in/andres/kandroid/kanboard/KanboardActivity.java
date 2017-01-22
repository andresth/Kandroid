package in.andres.kandroid.kanboard;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Thomas Andres on 07.01.17.
 */

public class KanboardActivity implements Serializable {
    private String Title;
    private String Content;
    private String Creator;
    private String CreatorUserName;
    private int CreatorId;
    private int Id;
    private int ProjectId;
    private int TaskId;
    private Date DateCreation;

    public KanboardActivity(JSONObject json) {
        Title = json.optString("event_title");
        Content = json.optString("event_content");
        Creator = json.optString("author");
        CreatorUserName = json.optString("author_username");
        CreatorId = json.optInt("creator_id");
        Id = json.optInt("id");
        ProjectId = json.optInt("project_id");
        TaskId = json.optInt("task_id");
        DateCreation = new Date(json.optLong("date_creation") * 1000);
    }

    public String getTitle() {
        return Title;
    }

    public String getContent() {
        return Content;
    }

    public String getCreator() {
        return Creator;
    }

    public String getCreatorUserName() {
        return CreatorUserName;
    }

    public int getCreatorId() {
        return CreatorId;
    }

    public int getId() {
        return Id;
    }

    public int getProjectId() {
        return ProjectId;
    }

    public int getTaskId() {
        return TaskId;
    }

    public Date getDateCreation() {
        return DateCreation;
    }

    @Override
    public String toString() {
        return this.Title + " " + this.DateCreation.toString();
    }
}
