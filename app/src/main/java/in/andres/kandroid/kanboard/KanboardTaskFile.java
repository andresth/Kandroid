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
public class KanboardTaskFile implements Serializable{
    private int id;
    private String name;
    private String path;
    private int taskId;
    private Date fileDate;
    private int userId;
    private long size;
    private String username;

    public KanboardTaskFile(JSONObject json) {
        id = json.optInt("id", -1);
        name = json.optString("name");
        path = json.optString("path");
        taskId = json.optInt("task_id", -1);
        userId = json.optInt("user_id", -1);
        size = json.optLong("size", 0);
        username = json.optString("username");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public long getSize() {
        return size;
    }
}
