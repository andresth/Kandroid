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

import org.json.JSONObject;

import java.io.Serializable;

@SuppressWarnings("unused")
public class KanboardSwimlane implements Serializable {
    private int Id;
    private String Name;
    private String Description;
    private int Position;
    private int ProjectId;
    private boolean IsActive;
    private boolean IsDefault = false;

    public KanboardSwimlane(JSONObject swimlane) {
        if (swimlane.has("default_swimlane")) {
            Id = 0;
            Name = swimlane.optString("default_swimlane");
            IsActive = KanboardAPI.StringToBoolean(swimlane.optString("show_default_swimlane"));
            IsDefault = true;
        } else {
            Id = swimlane.optInt("id");
            Name = swimlane.optString("name");
            Description = swimlane.optString("description");
            Position = swimlane.optInt("position");
            ProjectId = swimlane.optInt("project_id");
            IsActive = KanboardAPI.StringToBoolean(swimlane.optString("is_active"));
        }
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public int getPosition() {
        return Position;
    }

    public int getProjectId() {
        return ProjectId;
    }

    public boolean getIsActive() {
        return IsActive;
    }

    public boolean getIsDefault() {
        return IsDefault;
    }

    @Override
    public String toString() {
        return this.Name;
    }
}
