package in.andres.kandroid.kanboard;

import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by Thomas Andres on 01.01.17.
 */

public class KanboardTask implements Comparable<KanboardTask>, Serializable {
    private int ID;
    private int ProjectID;
    private int ColumnID;
    private int SwimlaneID;
    private int CategoryID;
    private int CreatorID;
    private int OwnerID;
    private int Priority;
    private int Position;
    private boolean IsActive;
    private String Title;
    private String Description;
    private URL Url;
    private double TimeEstimated;
    private double TimeSpent;
    // Dashboard properties
    private String ProjectName;
    private String ColumnTitle;
    private Date DueDate;
    private Date CompletedDate;
    private Date StartedDate;
    private Date CreationDate;
    private Date ModificationDate;
    private Date MovedDate;
    // TODO: getTaskByID has additional properties
    // TODO: add color

    public KanboardTask(JSONObject json) throws MalformedURLException {
        ID = json.optInt("id", -1);
        ProjectID = json.optInt("project_id", -1);
        ColumnID = json.optInt("column_id", -1);
        SwimlaneID = json.optInt("swimlane_id", -1);
        CategoryID = json.optInt("category_id", -1);
        CreatorID = json.optInt("creator_id", -1);
        OwnerID = json.optInt("owner_id", -1);
        Priority = json.optInt("priority", -1);
        Position = json.optInt("position", -1);
        IsActive = KanboardAPI.StringToBoolean(json.optString("is_active"));
        Title = json.optString("title");
        Description = json.optString("description");
        if (json.has("url"))
            Url = new URL(json.optString("url"));
        else
            Url = null;
        TimeEstimated = json.optDouble("time_estimated");
        TimeSpent = json.optDouble("time_spent");
        // Dashboard properties
        ProjectName = json.optString("project_name");
        ColumnTitle = json.optString("column_title");
        long tmpTime = json.optLong("date_due");
        // Kanboard returns 0 when there was no time set
        if (tmpTime > 0)
            DueDate = new Date(tmpTime * 1000);
        else
            DueDate = null;
        tmpTime = json.optLong("date_completed");
        if (tmpTime > 0)
            CompletedDate = new Date(tmpTime * 1000);
        else
            CompletedDate = null;
        tmpTime = json.optLong("date_started");
        if (tmpTime > 0)
            StartedDate = new Date(tmpTime * 1000);
        else
            StartedDate = null;
        tmpTime = json.optLong("date_creation");
        if (tmpTime > 0)
            CreationDate = new Date(tmpTime * 1000);
        else
            CreationDate = null;
        tmpTime = json.optLong("date_modification");
        if (tmpTime > 0)
            ModificationDate = new Date(tmpTime * 1000);
        else
            ModificationDate = null;
        tmpTime = json.optLong("date_moved");
        if (tmpTime > 0)
            MovedDate = new Date(tmpTime * 1000);
        else
            MovedDate = null;

    }

    public int getId() {
        return ID;
    }

    public int getProjectId() {
        return ProjectID;
    }

    public int getColumnId() {
        return ColumnID;
    }

    public int getSwimlaneId() {
        return SwimlaneID;
    }

    public int getCategoryId() {
        return CategoryID;
    }

    public int getOwnerId() {
        return OwnerID;
    }

    public int getCreatorId() {
        return CreatorID;
    }

    public int getPriority() {
        return Priority;
    }

    public int getPosition() {
        return Position;
    }

    public boolean getIsActive() {
        return IsActive;
    }

    public String getDescription() {
        return Description;
    }

    public String getTitle() {
        return Title;
    }

    public URL getUrl() {
        return Url;
    }

    public double getTimeEstimated() {
        return TimeEstimated;
    }

    public double getTimeSpent() {
        return TimeSpent;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public String getColumnTitle() {
        return ColumnTitle;
    }

    public Date getDateCompleted() {
        return CompletedDate;
    }

    public Date getDateStarted() {
        return StartedDate;
    }

    public Date getDateDue() {
        return DueDate;
    }

    public Date getDateCreation() {
        return CreationDate;
    }

    public Date getDateModification() {
        return ModificationDate;
    }

    public Date getDateMoved() {
        return MovedDate;
    }

    @Override
    public int compareTo(KanboardTask o) {
        return this.Title.compareTo(o.Title);
    }

    @Override
    public String toString() {
        return this.Title;
    }
}
