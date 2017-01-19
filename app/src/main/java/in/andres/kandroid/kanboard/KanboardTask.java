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
    public final int ID;
    public final int ProjectID;
    public final int ColumnID;
    public final int SwimlaneID;
    public final int CategoryID;
    public final int CreatorID;
    public final int OwnerID;
    public final int Priority;
    public final int Position;
    public final boolean IsActive;
    public final String Title;
    public final String Description;
    public final URL Url;
    public final double TimeEstimated;
    public final double TimeSpent;
    // Dashboard properties
    public final String ProjectName;
    public final String ColumnTitle;
    public final Date DueDate;
    public final Date CompletedDate;
    public final Date StartedDate;
    public final Date CreationDate;
    public final Date ModificationDate;
    public final Date MovedDate;
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

    @Override
    public int compareTo(KanboardTask o) {
        return this.Title.compareTo(o.Title);
    }

    @Override
    public String toString() {
        return this.Title;
    }
}
