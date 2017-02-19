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
import java.util.Date;

@SuppressWarnings("unused")
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
