/*
 * Copyright 2017 Thomas Andres
 *
 * This file is part of Kandroid.
 *
 * Kandroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kandroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package in.andres.kandroid.kanboard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class KanboardProject implements Comparable<KanboardProject>, Serializable {
    private int Id;
    private String Name;
    private int OwnerId;
    private String Description;
    private String Identifier;
    private String Token;
    private boolean IsActive;
    private boolean IsPublic;
    private boolean IsPrivate;
    private boolean IsEverybodyAllowed;
    private boolean HasHiddenSwimlanes = false;
    private Date StartDate;
    private Date EndDate;
    private Date LastModified;
    private int NumberActiveTasks;
    private URL ListURL;
    private URL BoardURL;
    private URL CalendarURL;
    private List<KanboardColumn> Columns;
    private List<KanboardCategory> Categories;
    private List<KanboardSwimlane> Swimlanes;
    private List<KanboardTask> ActiveTasks;
    private Dictionary<Integer, Dictionary<Integer, List<KanboardTask>>> GroupedActiveTasks;
    private List<KanboardTask> InactiveTasks;
    private Dictionary<Integer, List<KanboardTask>> GroupedInactiveTasks;
    private List<KanboardTask> OverdueTasks;
    private Dictionary<Integer, List<KanboardTask>> GroupedOverdueTasks;
    private Dictionary<Integer, KanboardTask> TaskHashtable;
    private Dictionary<Integer, KanboardCategory> CategoryHashtable;
    private Dictionary<Integer, String> ProjectUsers;
    // TODO: add priority values to project details

    public KanboardProject(@NonNull JSONObject project) throws MalformedURLException {
        this(project, null, null, null, null, null, null, null);
    }

    public KanboardProject(@NonNull JSONObject project, @Nullable JSONArray columns, @Nullable JSONArray swimlanes,
                           @Nullable JSONArray categories, @Nullable JSONArray activetasks,
                           @Nullable JSONArray inactivetasks, @Nullable JSONArray overduetasks,
                           @Nullable JSONObject projectusers) throws MalformedURLException {
        Id = project.optInt("id");
        Name = project.optString("name");
        OwnerId = project.optInt("owner_id");
        Object desc = project.opt("description");
        Description = desc == null ? "" : desc.toString();
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
            if (urls.has("calendar")) {
                CalendarURL = new URL(urls.optString("calendar"));
            } else {
                CalendarURL = null;
            }
        } else {
            ListURL = null;
            BoardURL = null;
            CalendarURL = null;
        }

        GroupedActiveTasks = new Hashtable<>();
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
                GroupedActiveTasks.put(tmpCol.getId(), new Hashtable<Integer, List<KanboardTask>>());
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
                    GroupedActiveTasks.get(c.getId()).put(tmpSwim.getId(), new ArrayList<KanboardTask>());

                GroupedInactiveTasks.put(tmpSwim.getId(), new ArrayList<KanboardTask>());
                GroupedOverdueTasks.put(tmpSwim.getId(), new ArrayList<KanboardTask>());
                }
            }
        }

        Categories = new ArrayList<>();
        if (categories != null) {
            for (int i = 0; i < categories.length(); i++) {
                KanboardCategory tmpCategory = new KanboardCategory(categories.optJSONObject(i));
                Categories.add(tmpCategory);
                CategoryHashtable.put(tmpCategory.getId(), tmpCategory);
            }
        }

        ActiveTasks = new ArrayList<>();
        if (activetasks != null)
            for (int i = 0; i < activetasks.length(); i++) {
                KanboardTask tmpActiveTask = new KanboardTask(activetasks.optJSONObject(i));
                TaskHashtable.put(tmpActiveTask.getId(), tmpActiveTask);
                ActiveTasks.add(tmpActiveTask);
                if (Swimlanes.size() > 0) {
                    if (((Hashtable<Integer, List<KanboardTask>>) GroupedActiveTasks.get(tmpActiveTask.getColumnId())).containsKey(tmpActiveTask.getSwimlaneId())) {
                        GroupedActiveTasks.get(tmpActiveTask.getColumnId()).get(tmpActiveTask.getSwimlaneId()).add(tmpActiveTask);
                    } else {
                        HasHiddenSwimlanes = true;
                    }
                }
            }

        InactiveTasks = new ArrayList<>();
        if (inactivetasks != null)
            for (int i = 0; i < inactivetasks.length(); i++) {
                KanboardTask tmpInactiveTask = new KanboardTask(inactivetasks.optJSONObject(i));
                TaskHashtable.put(tmpInactiveTask.getId(), tmpInactiveTask);
                InactiveTasks.add(tmpInactiveTask);
                if (Swimlanes.size() > 0) {
                    if (((Hashtable<Integer, List<KanboardTask>>) GroupedInactiveTasks).containsKey(tmpInactiveTask.getSwimlaneId())) {
                        GroupedInactiveTasks.get(tmpInactiveTask.getSwimlaneId()).add(tmpInactiveTask);
                    } else {
                        HasHiddenSwimlanes = true;
                    }
                }
            }

        OverdueTasks = new ArrayList<>();
        if (overduetasks != null)
            for (int i = 0; i < overduetasks.length(); i++) {
                KanboardTask tmpOverdueTask = new KanboardTask(overduetasks.optJSONObject(i));
                OverdueTasks.add(TaskHashtable.get(tmpOverdueTask.getId()));
                if (Swimlanes.size() > 0) {
                    if (((Hashtable<Integer, List<KanboardTask>>) GroupedOverdueTasks).containsKey(tmpOverdueTask.getSwimlaneId())) {
                        GroupedOverdueTasks.get(TaskHashtable.get(tmpOverdueTask.getId()).getSwimlaneId()).add(TaskHashtable.get(tmpOverdueTask.getId()));
                    } else {
                        HasHiddenSwimlanes = true;
                    }
                }
            }

        ProjectUsers = new Hashtable<>();
        if (projectusers != null) {
            for (Iterator<String> iter = projectusers.keys(); iter.hasNext();) {
                String key = iter.next();
                ProjectUsers.put(Integer.parseInt(key), projectusers.optString(key));
            }
        }
    }

    public void setExtra(@NonNull List<KanboardColumn> columns, @NonNull List<KanboardSwimlane> swimlanes,
                          @NonNull List<KanboardCategory> categories, @NonNull List<KanboardTask> activetasks,
                          @NonNull List<KanboardTask> inactivetasks, @NonNull List<KanboardTask> overduetasks,
                          @NonNull Dictionary<Integer, String> projectusers) {
        Columns = columns;
        Swimlanes = swimlanes;
        for (KanboardColumn col: Columns) {
            Dictionary<Integer, List<KanboardTask>> tmpTable = new Hashtable<>();
            for (KanboardSwimlane swim: Swimlanes) {
                tmpTable.put(swim.getId(), new ArrayList<KanboardTask>());
                GroupedInactiveTasks.put(swim.getId(), new ArrayList<KanboardTask>());
                GroupedOverdueTasks.put(swim.getId(), new ArrayList<KanboardTask>());
            }
            GroupedActiveTasks.put(col.getId(), tmpTable);
        }

        Categories = categories;
        for (KanboardCategory cat: Categories)
            CategoryHashtable.put(cat.getId(), cat);

        ActiveTasks = activetasks;
        Collections.sort(ActiveTasks);
        for (KanboardTask task: ActiveTasks) {
            if (Swimlanes.size() > 0) {
                if (((Hashtable<Integer, List<KanboardTask>>) GroupedActiveTasks.get(task.getColumnId())).containsKey(task.getSwimlaneId())) {
                    GroupedActiveTasks.get(task.getColumnId()).get(task.getSwimlaneId()).add(task);
                } else {
                    HasHiddenSwimlanes = true;
                }
            }
            TaskHashtable.put(task.getId(), task);
        }

        InactiveTasks = inactivetasks;
        Collections.sort(InactiveTasks);
        for (KanboardTask task: InactiveTasks) {
            if (Swimlanes.size() > 0) {
                if (((Hashtable<Integer, List<KanboardTask>>) GroupedInactiveTasks).containsKey(task.getSwimlaneId())) {
                    GroupedInactiveTasks.get(task.getSwimlaneId()).add(task);
                } else {
                    HasHiddenSwimlanes = true;
                }
            }
            TaskHashtable.put(task.getId(), task);
        }

        for (KanboardTask task: overduetasks) {
            OverdueTasks.add(TaskHashtable.get(task.getId()));
            if (Swimlanes.size() > 0) {
                if (((Hashtable<Integer, List<KanboardTask>>) GroupedOverdueTasks).containsKey(task.getSwimlaneId())) {
                    GroupedOverdueTasks.get(TaskHashtable.get(task.getId()).getSwimlaneId()).add(TaskHashtable.get(task.getId()));
                } else {
                    HasHiddenSwimlanes = true;
                }
            }
        }
        Collections.sort(OverdueTasks);
        Enumeration<Integer> enumSwimlanes = GroupedOverdueTasks.keys();
        while (enumSwimlanes.hasMoreElements()) {
            Integer swimKey = enumSwimlanes.nextElement();
            Collections.sort(GroupedOverdueTasks.get(swimKey));
        }
        ProjectUsers = projectusers;
    }

    public int getId() {
        return Id;
    }

    @Nullable
    public String getName() {
        return Name;
    }

    public int getOwnerId() {
        return OwnerId;
    }

    @NonNull
    public String getDescription() {
        return Description;
    }

    @Nullable
    public String getIdentifier() {
        return Identifier;
    }

    @Nullable
    public String getToken() {
        return Token;
    }

    public boolean getIsActive() {
        return IsActive;
    }

    public boolean getIsPublic() {
        return IsPublic;
    }

    public boolean getIsPrivate() {
        return IsPrivate;
    }

    public boolean getIsEverybodyAllowed() {
        return IsEverybodyAllowed;
    }

    @Nullable
    public Date getStartDate() {
        return StartDate;
    }

    @Nullable
    public Date getEndDate() {
        return EndDate;
    }

    @Nullable
    public Date getLastModified() {
        return LastModified;
    }

    public int getNumberActiveTasks() {
        return NumberActiveTasks;
    }

    @Nullable
    public URL getListURL() {
        return ListURL;
    }

    @Nullable
    public URL getBoardURL() {
        return BoardURL;
    }

    @Nullable
    public URL getCalendarURL() {
        return CalendarURL;
    }

    @NonNull
    public List<KanboardColumn> getColumns() {
        return Columns;
    }

    @NonNull
    public List<KanboardCategory> getCategories() {
        return Categories;
    }

    @NonNull
    public List<KanboardSwimlane> getSwimlanes() {
        return Swimlanes;
    }

    @NonNull
    public List<KanboardTask> getActiveTasks() {
        return ActiveTasks;
    }

    @NonNull
    public Dictionary<Integer, Dictionary<Integer, List<KanboardTask>>> getGroupedActiveTasks() {
        return GroupedActiveTasks;
    }

    @NonNull
    public List<KanboardTask> getInactiveTasks() {
        return InactiveTasks;
    }

    public boolean hasHiddenSwimlanes() {
        return HasHiddenSwimlanes;
    }

    @NonNull
    public Dictionary<Integer, List<KanboardTask>> getGroupedInactiveTasks() {
        return GroupedInactiveTasks;
    }

    @NonNull
    public List<KanboardTask> getOverdueTasks() {
        return OverdueTasks;
    }

    @NonNull
    public Dictionary<Integer, List<KanboardTask>> getGroupedOverdueTasks() {
        return GroupedOverdueTasks;
    }

    @NonNull
    public Dictionary<Integer, KanboardTask> getTaskHashtable() {
        return TaskHashtable;
    }

    @NonNull
    public Dictionary<Integer, KanboardCategory> getCategoryHashtable() {
        return CategoryHashtable;
    }

    @NonNull
    public Dictionary<Integer, String> getProjectUsers() {
        return ProjectUsers;
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
