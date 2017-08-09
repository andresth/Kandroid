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
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import in.andres.kandroid.Constants;

@SuppressWarnings("unused")
public class KanboardDashboard implements Serializable {
    private List<KanboardProject> Projects;
    private List<KanboardTask> Tasks;
    private List<KanboardSubtask> Subtasks;
    private Dictionary<Integer, List<KanboardTask>> GroupedTasks;
    private List<KanboardTask> OverdueTasks = new ArrayList<>();
    private List<KanboardActivity> Activities = new ArrayList<>();

    private boolean newDashFormat = false;

    public KanboardDashboard(@NonNull Object dashboard) throws MalformedURLException {
        this(dashboard, null, null);
    }

    public KanboardDashboard(@NonNull Object dashboard, @Nullable JSONArray overdue, @Nullable JSONArray activities) throws MalformedURLException {
        GroupedTasks = new Hashtable<>();
        Projects = new ArrayList<>();
        if (dashboard instanceof JSONObject) {
            Log.i(Constants.TAG, "Old Dashboard");
            JSONObject dash = (JSONObject) dashboard;
            JSONArray projects = dash.optJSONArray("projects");
            if (projects != null)
                for (int i = 0; i < projects.length(); i++) {
                    KanboardProject tmpProject = new KanboardProject(projects.optJSONObject(i));
                    Projects.add(tmpProject);
                    GroupedTasks.put(tmpProject.getId(), new ArrayList<KanboardTask>());
                }
            Tasks = new ArrayList<>();
            JSONArray tasks = dash.optJSONArray("tasks");
            if (tasks != null)
                for (int i = 0; i < tasks.length(); i++) {
                    KanboardTask tmpTask = new KanboardTask(tasks.optJSONObject(i));
                    Tasks.add(tmpTask);
                    GroupedTasks.get(tmpTask.getProjectId()).add(tmpTask);
                }
            Subtasks = new ArrayList<>();
            JSONArray subtasks = dash.optJSONArray("subtasks");
            if (subtasks != null)
                for (int i = 0; i < subtasks.length(); i++)
                    Subtasks.add(new KanboardSubtask(subtasks.optJSONObject(i)));
        } else {
            Log.i(Constants.TAG, "New Dashboard");
            newDashFormat = true;
            Tasks = new ArrayList<>();
            JSONArray dash = (JSONArray) dashboard;
            for (int i = 0; i < dash.length(); i++) {
                JSONObject item = dash.optJSONObject(i);
                KanboardTask tmpTask = new KanboardTask(item);
                Tasks.add(tmpTask);
                if (!((Hashtable) GroupedTasks).containsKey(tmpTask.getProjectId())) {
                    GroupedTasks.put(tmpTask.getProjectId(), new ArrayList<KanboardTask>());
                    try {
                        Projects.add(new KanboardProject(new JSONObject(String.format("{\"id\": \"%d\",\"name\": \"%s\"}", tmpTask.getProjectId(), tmpTask.getProjectName()))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                GroupedTasks.get(tmpTask.getProjectId()).add(tmpTask);
            }
        }

        if (overdue != null) {
            OverdueTasks = new ArrayList<>();
            for (int i = 0; i < overdue.length(); i++) {
                OverdueTasks.add(new KanboardTask(overdue.optJSONObject(i)));
            }
        }

        if (activities != null) {
            Activities = new ArrayList<>();
            for (int i = 0; i < activities.length(); i++) {
                Activities.add(new KanboardActivity(activities.optJSONObject(i)));
            }
        }
    }

    public void setExtra(List<KanboardTask> overdueTasks, List<KanboardActivity> activities, List<KanboardProject> projectList) {
        OverdueTasks = overdueTasks;
        Activities = activities;
        if (newDashFormat) {
            List<KanboardProject> tmpProject = new ArrayList<>();
            for (KanboardProject pn : projectList) {
                for (KanboardProject po : Projects) {
                    if (po.getId() == pn.getId()) {
                        tmpProject.add(pn);
                        break;
                    }
                }
            }
            if (projectList.size() == Projects.size())
                Projects = tmpProject;
        }
    }

    public List<KanboardProject> getProjects() {
        return Projects;
    }

    public List<KanboardTask> getTasks() {
        return Tasks;
    }

    public List<KanboardSubtask> getSubtasks() {
        return Subtasks;
    }

    public Dictionary<Integer, List<KanboardTask>> getGroupedTasks() {
        return GroupedTasks;
    }

    public List<KanboardTask> getOverdueTasks() {
        return OverdueTasks;
    }

    public List<KanboardActivity> getActivities() {
        return Activities;
    }
}
