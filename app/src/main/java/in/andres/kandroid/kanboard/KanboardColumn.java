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

import org.json.JSONObject;

import java.io.Serializable;

@SuppressWarnings("unused")
public class KanboardColumn implements Comparable<KanboardColumn>, Serializable {
    private int Id;
    private int Position;
    private int TaskLimit;
    private int NumberTasks;
    private String Title;
    private String Description;
    private int ProjectId;
    private boolean HideInDashboard;

    public int getId() {
        return Id;
    }

    public int getPosition() {
        return Position;
    }

    public int getTaskLimit() {
        return TaskLimit;
    }

    public int getNumberTasks() {
        return NumberTasks;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public int getProjectId() {
        return ProjectId;
    }

    public boolean getHideInDashboard() {
        return HideInDashboard;
    }

    public KanboardColumn(JSONObject json) {
        Id = json.optInt("id");
        Position = json.optInt("position");
        TaskLimit = json.optInt("task_limit");
        NumberTasks = json.optInt("nb_tasks");
        Title = json.optString("title");
        Description = json.optString("description");
        ProjectId = json.optInt("project_id");
        HideInDashboard = KanboardAPI.StringToBoolean(json.optString("hide_in_dashboard"));
    }

    @Override
    public int compareTo(@NonNull KanboardColumn o) {
        return this.Title.compareTo(o.Title);
    }

    @Override
    public String toString() {
        return this.Title;
    }
}
