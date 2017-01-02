package in.andres.kandroid.kanboard;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Andres on 01.01.17.
 */


public class KanboardProject implements Comparable<KanboardProject> {
    public final int ID;
    public final String Name;
    public final int OwnerID;
    public final String Description;
    public final String Identifier;
    public final String Token;
    public final boolean IsActive;
    public final boolean IsPublic;
    public final boolean IsPrivate;
    public final boolean IsEverybodyAllowed;
    public final String StartDate;
    public final String EndDate;
    public final String LastModified;
    public final int NumberActiveTasks;
    public final URL ListURL;
    public final URL BoardURL;
    public final URL CalendarURL;
    public final List<KanboardColumn> Columns;
    // TODO: add priority values to project details
    // TODO: handle dates correctly
    // TODO: getProjectById might have additional properties!

    public KanboardProject(JSONObject json) throws MalformedURLException {
        ID = json.optInt("id");
        Name = json.optString("name");
        OwnerID = json.optInt("owner_id");
        Description = json.optString("description");
        Identifier = json.optString("identifier");
        Token = json.optString("token");
        IsActive = KanboardAPI.StringToBoolean(json.optString("is_active"));
        IsPublic = KanboardAPI.StringToBoolean(json.optString("is_public"));
        IsPrivate = KanboardAPI.StringToBoolean(json.optString("is_private"));
        IsEverybodyAllowed = KanboardAPI.StringToBoolean(json.optString("is_everybody_allowed"));
        StartDate = json.optString("start_date");
        EndDate = json.optString("end_date");
        LastModified = json.optString("last_modified");
        NumberActiveTasks = json.optInt("nb_active_tasks");
        JSONObject urls = json.optJSONObject("url");
        if (urls != null) {
            ListURL = new URL(urls.optString("list"));
            BoardURL = new URL(urls.optString("board"));
            CalendarURL = new URL(urls.optString("calendar"));
        } else {
            ListURL = null;
            BoardURL = null;
            CalendarURL = null;
        }
        Columns = new ArrayList<>();
        JSONArray cols = json.optJSONArray("columns");
        if (cols != null)
            for (int i = 0; i < cols.length(); i++)
                Columns.add(new KanboardColumn(cols.optJSONObject(i)));
    }

    @Override
    public int compareTo(KanboardProject o) {
        return this.Name.compareTo(o.Name);
    }
}
