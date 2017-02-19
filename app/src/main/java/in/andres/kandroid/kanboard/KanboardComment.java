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
import java.util.Date;

@SuppressWarnings("unused")
public class KanboardComment implements Serializable {
    private int id;
    private Date date_creation;
    private Date date_modification;
    private int task_id;
    private int user_id;
    private String content;
    private String username;
    private String name;
    private String email;
    private String avatar_path;

    public KanboardComment() {}

    public KanboardComment(@NonNull JSONObject comment) {
        id = comment.optInt("id", -1);
        long tmpTime = comment.optLong("date_creation");
        if (tmpTime > 0)
            date_creation = new Date(tmpTime * 1000);
        else
            date_creation = null;
        tmpTime = comment.optLong("date_modification");
        if (tmpTime > 0)
            date_modification = new Date(tmpTime * 1000);
        else
            date_modification = null;
        task_id = comment.optInt("task_id");
        user_id = comment.optInt("user_id");
        content = comment.optString("comment");
        username = comment.optString("username");
        name = comment.optString("name");
        email = comment.optString("email");
        avatar_path = comment.optString("avatar_path");
    }

    public int getId() {
        return this.id;
    }

    @Nullable
    public Date getDateCreation() {
        return this.date_creation;
    }

    @Nullable
    public Date getDateModification() {
        return date_modification;
    }

    public int getTaskId() {
        return this.task_id;
    }

    public void setTaskID(int taskid) {
        this.task_id = taskid;
    }

    public int getUserId() {
        return this.user_id;
    }

    public void setUserId(int userid) {
        this.user_id = user_id;
    }

    @Nullable
    public String getContent() {
        return this.content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @Nullable
    public String getUsername() {
        return this.username;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    @Nullable
    public String getEmail() {
        return this.email;
    }

    @Nullable
    public String getAvatarPath() {
        return this.avatar_path;
    }

    @Override
    public String toString() {
        return this.content;
    }
}
