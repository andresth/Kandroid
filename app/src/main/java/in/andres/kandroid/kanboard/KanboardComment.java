package in.andres.kandroid.kanboard;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Thomas Andres on 20.01.17.
 */

public class KanboardComment implements Serializable {
    private int id;
    private Date date_creation;
    private int task_id;
    private int user_id;
    private String content;
    private String username;
    private String name;
    private String email;
    private String avatar_path;

    public KanboardComment() {}

    public KanboardComment(JSONObject comment) {
        id = comment.optInt("id", -1);
        long tmpTime = comment.optLong("date_creation");
        if (tmpTime > 0)
            date_creation = new Date(tmpTime * 1000);
        else
            date_creation = null;
        task_id = comment.optInt("task_id");
        user_id = comment.optInt("user_id");
        content = comment.optString("content");
        username = comment.optString("username");
        name = comment.optString("name");
        email = comment.optString("email");
        avatar_path = comment.optString("avatar_path");
    }

    public int getId() {
        return this.id;
    }

    public Date getDateCreation() {
        return this.date_creation;
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

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return this.username;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getAvatarPath() {
        return this.avatar_path;
    }

    @Override
    public String toString() {
        return this.content;
    }
}
