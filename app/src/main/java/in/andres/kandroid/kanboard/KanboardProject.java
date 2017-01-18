package in.andres.kandroid.kanboard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Thomas Andres on 01.01.17.
 */


public class KanboardProject implements Comparable<KanboardProject>, Serializable {
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
    public final Date StartDate;
    public final Date EndDate;
    public final Date LastModified;
    public final int NumberActiveTasks;
    public final URL ListURL;
    public final URL BoardURL;
    public final URL CalendarURL;
    public final List<KanboardColumn> Columns;
    public final List<KanboardCategory> Categories;
    public final List<KanboardSwimlane> Swimlanes;
    public final List<KanboardTask> ActiveTasks;
    public final Dictionary<Integer, Dictionary<Integer, List<KanboardTask>>> GroupedActiveTasks;
    public final List<KanboardTask> InactiveTasks;
    public final Dictionary<Integer, List<KanboardTask>> GroupedInactiveTasks;
    public final List<KanboardTask> OverdueTasks;
    public final Dictionary<Integer, List<KanboardTask>> GroupedOverdueTasks;
    public final Dictionary<Integer, KanboardTask> TaskHashtable;
    public final Dictionary<Integer, KanboardCategory> CategoryHashtable;
    // TODO: add priority values to project details
    // TODO: getProjectById might have additional properties!

    public KanboardProject(@NonNull JSONObject project) throws MalformedURLException {
        this(project, null, null, null, null, null, null);
    }

    public KanboardProject(@NonNull JSONObject project, @Nullable JSONArray columns, @Nullable JSONArray swimlanes,
                           @Nullable JSONArray categories, @Nullable JSONArray activetasks,
                           @Nullable JSONArray inactivetasks, @Nullable JSONArray overduetasks) throws MalformedURLException {
        ID = project.optInt("id");
        Name = project.optString("name");
        OwnerID = project.optInt("owner_id");
        Object desc = project.opt("description");
        Description = desc.equals(null) ? null : desc.toString();
        Identifier = project.optString("identifier");
        Token = project.optString("token");
        IsActive = KanboardAPI.StringToBoolean(project.optString("is_active"));
        IsPublic = KanboardAPI.StringToBoolean(project.optString("is_public"));
        IsPrivate = KanboardAPI.StringToBoolean(project.optString("is_private"));
        IsEverybodyAllowed = KanboardAPI.StringToBoolean(project.optString("is_everybody_allowed"));
        long tmpDate = project.optLong("start_date");
        if (tmpDate > 0)
            StartDate = new Date(tmpDate * 1000);
        else
            StartDate = null;
        tmpDate = project.optLong("end_date");
        if (tmpDate > 0)
            EndDate = new Date(tmpDate * 1000);
        else
            EndDate = null;
        tmpDate = project.optLong("last_modified");
        if (tmpDate > 0)
            LastModified = new Date(tmpDate * 1000);
        else
            LastModified = null;
        NumberActiveTasks = project.optInt("nb_active_tasks");
        JSONObject urls = project.optJSONObject("url");
        if (urls != null) {
            ListURL = new URL(urls.optString("list"));
            BoardURL = new URL(urls.optString("board"));
            CalendarURL = new URL(urls.optString("calendar"));
        } else {
            ListURL = null;
            BoardURL = null;
            CalendarURL = null;
        }

        GroupedActiveTasks = new Hashtable<Integer, Dictionary<Integer, List<KanboardTask>>>();
        GroupedInactiveTasks = new Hashtable<>();
        GroupedOverdueTasks = new Hashtable<>();
        TaskHashtable = new Hashtable<>();
        CategoryHashtable = new Hashtable<>();

        Columns = new ArrayList<>();
        JSONArray cols = project.optJSONArray("columns");
        if (columns != null) {
            for (int i = 0; i < columns.length(); i++) {
                KanboardColumn tmpCol = new KanboardColumn(columns.optJSONObject(i));
                Columns.add(tmpCol);
                GroupedActiveTasks.put(tmpCol.ID, new Hashtable<Integer, List<KanboardTask>>());
            }
        }
        else if (cols != null) {
            for (int i = 0; i < cols.length(); i++)
                Columns.add(new KanboardColumn(cols.optJSONObject(i)));
        }

        Swimlanes = new ArrayList<>();
        if (swimlanes != null) {
            for (int i = 0; i < swimlanes.length(); i++) {
                KanboardSwimlane tmpSwim = new KanboardSwimlane(swimlanes.optJSONObject(i));
                Swimlanes.add(tmpSwim);
                for (KanboardColumn c: Columns) {
                    GroupedActiveTasks.get(c.ID).put(tmpSwim.ID, new ArrayList<KanboardTask>());

                GroupedInactiveTasks.put(tmpSwim.ID, new ArrayList<KanboardTask>());
                GroupedOverdueTasks.put(tmpSwim.ID, new ArrayList<KanboardTask>());
                }
            }
        }

        Categories = new ArrayList<>();
        if (categories != null) {
            for (int i = 0; i < categories.length(); i++) {
                KanboardCategory tmpCategory = new KanboardCategory(categories.optJSONObject(i));
                Categories.add(tmpCategory);
                CategoryHashtable.put(tmpCategory.ID, tmpCategory);
            }
        }

        ActiveTasks = new ArrayList<>();
        if (activetasks != null)
            for (int i = 0; i < activetasks.length(); i++) {
                KanboardTask tmpActiveTask = new KanboardTask(activetasks.optJSONObject(i));
                TaskHashtable.put(tmpActiveTask.ID, tmpActiveTask);
                ActiveTasks.add(tmpActiveTask);
                GroupedActiveTasks.get(tmpActiveTask.ColumnID).get(tmpActiveTask.SwimlaneID).add(tmpActiveTask);
            }

        InactiveTasks = new ArrayList<>();
        if (inactivetasks != null)
            for (int i = 0; i < inactivetasks.length(); i++) {
                KanboardTask tmpInactiveTask = new KanboardTask(inactivetasks.optJSONObject(i));
                TaskHashtable.put(tmpInactiveTask.ID, tmpInactiveTask);
                InactiveTasks.add(tmpInactiveTask);
                GroupedInactiveTasks.get(tmpInactiveTask.SwimlaneID).add(tmpInactiveTask);
            }

        OverdueTasks = new ArrayList<>();
        if (overduetasks != null)
            for (int i = 0; i < overduetasks.length(); i++) {
                KanboardTask tmpOverdueTask = new KanboardTask(overduetasks.optJSONObject(i));
                OverdueTasks.add(TaskHashtable.get(tmpOverdueTask.ID));
                GroupedOverdueTasks.get(TaskHashtable.get(tmpOverdueTask.ID).SwimlaneID).add(TaskHashtable.get(tmpOverdueTask.ID));
            }
    }

    @Override
    public int compareTo(@NonNull KanboardProject o) {
        return this.Name.compareTo(o.Name);
    }

    @Override
    public String toString() {
        return this.Name;
    }
}
