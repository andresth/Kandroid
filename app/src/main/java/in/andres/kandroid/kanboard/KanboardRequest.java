package in.andres.kandroid.kanboard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringEscapeUtils;

public class KanboardRequest {
    public final String Command;
    public final String[] JSON;

    private KanboardRequest(@NonNull String command, @NonNull String[] json) {
        this.Command = command;
        this.JSON = json;
    }

    //region Kanboard API
    @NonNull
    public static KanboardRequest getMe() {
        return new KanboardRequest("getMe", new String[] {"{\"jsonrpc\": \"2.0\", \"method\": \"getMe\", \"id\": 1}"});
    }

    @NonNull
    public static KanboardRequest getMyProjectsList() {
        return new KanboardRequest("getMyProjectsList", new String[] {"{\"jsonrpc\": \"2.0\", \"method\": \"getMyProjectsList\", \"id\": 1}"});
    }

    @NonNull
    public static KanboardRequest getMyDashboard() {
        return new KanboardRequest("getMyDashboard", new  String[] {"{\"jsonrpc\": \"2.0\", \"method\": \"getMyDashboard\", \"id\": 1}"});
    }

    @NonNull
    public static KanboardRequest getMyOverdueTasks() {
        return new KanboardRequest("getMyOverdueTasks", new String[] {"{\"jsonrpc\": \"2.0\", \"method\": \"getMyOverdueTasks\", \"id\": 1}"});
    }

    @NonNull
    public static KanboardRequest getMyActivityStream() {
        return new KanboardRequest("getMyActivityStream", new String[] {"{\"jsonrpc\": \"2.0\", \"method\": \"getMyActivityStream\", \"id\": 1}"});
    }

    @NonNull
    public static KanboardRequest getProjectById(int projectid) {
        return new KanboardRequest("getProjectById", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getProjectById\",\n" +
                "    \"id\": 1,\n" +
                "    \"params\": {\n" +
                "        \"project_id\": %d\n" +
                "    }\n" +
                "}", projectid)});
    }

    @NonNull
    public static KanboardRequest getProjectUsers(int projectid) {
        return new KanboardRequest("getProjectUsers", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getProjectUsers\",\n" +
                "    \"id\": 1601016721,\n" +
                "    \"params\": [\n" +
                "        \"%d\"\n" +
                "    ]\n" +
                "}", projectid)});
    }

    @NonNull
    public static KanboardRequest getTask(int taskid) {
        return new KanboardRequest("getTask", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getTask\",\n" +
                "    \"id\": 700738119,\n" +
                "    \"params\": {\n" +
                "        \"task_id\": %d\n" +
                "    }\n" +
                "}", taskid)});
    }

    @NonNull
    public static KanboardRequest getAllTasks(int projectid, int status) {
        return new KanboardRequest("getAllTasks", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getAllTasks\",\n" +
                "    \"id\": 1,\n" +
                "    \"params\": {\n" +
                "        \"project_id\": %d,\n" +
                "        \"status_id\": %d\n" +
                "    }\n" +
                "}", projectid, status)});
    }

    @NonNull
    public static KanboardRequest getOverdueTasksByProject(int projectid) {
        return new KanboardRequest("getOverdueTasksByProject", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getOverdueTasksByProject\",\n" +
                "    \"id\": 133280317,\n" +
                "    \"params\": {\n" +
                "        \"project_id\": %d\n" +
                "    }\n" +
                "}", projectid)});
    }

    @NonNull
    public static KanboardRequest getSwimlane(int swimlaneid) {
        return new KanboardRequest("getSwimlane", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getSwimlane\",\n" +
                "    \"id\": 131071870,\n" +
                "    \"params\": [\n" +
                "        %d\n" +
                "    ]\n" +
                "}", swimlaneid)});
    }

    @NonNull
    public static KanboardRequest getDefaultSwimlane(int projectid) {
        return new KanboardRequest("getDefaultSwimlane", new String[] {String.format(
                "{\n" +
                        "    \"jsonrpc\": \"2.0\",\n" +
                        "    \"method\": \"getDefaultSwimlane\",\n" +
                        "    \"id\": 131071870,\n" +
                        "    \"params\": [\n" +
                        "        %d\n" +
                        "    ]\n" +
                        "}", projectid)});
    }

    @NonNull
    public static KanboardRequest getCategrory(int categoryid) {
        return new KanboardRequest("getCategory", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getCategory\",\n" +
                "    \"id\": 203539163,\n" +
                "    \"params\": {\n" +
                "        \"category_id\": %d\n" +
                "    }\n" +
                "}", categoryid)});
    }

    @NonNull
    public static KanboardRequest getColumns(int projectid) {
        return new KanboardRequest("getColumns", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getColumns\",\n" +
                "    \"id\": 1,\n" +
                "    \"params\": [\n" +
                "        %d\n" +
                "    ]\n" +
                "}", projectid)});
    }

    @NonNull
    public static KanboardRequest getActiveSwimlanes(int projectid) {
        return new KanboardRequest("getActiveSwimlanes", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getActiveSwimlanes\",\n" +
                "    \"id\": 934789422,\n" +
                "    \"params\": [\n" +
                "        %d\n" +
                "    ]\n" +
                "}", projectid)});
    }

    @NonNull
    public static KanboardRequest getAllCategories(int projectid) {
        return new KanboardRequest("getAllCategories", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getAllCategories\",\n" +
                "    \"id\": 1,\n" +
                "    \"params\": {\n" +
                "        \"project_id\": %d\n" +
                "    }\n" +
                "}", projectid)});
    }

    @NonNull
    public static KanboardRequest getAllComments(int projectid) {
        return new KanboardRequest("getAllComments", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getAllComments\",\n" +
                "    \"id\": 1,\n" +
                "    \"params\": {\n" +
                "        \"task_id\": %d\n" +
                "    }\n" +
                "}", projectid)});
    }

    @NonNull
    public static KanboardRequest createComment(int taskid, int userid, @NonNull String comment) {
        return new KanboardRequest("createComment", new String[] {String.format("" +
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"createComment\",\n" +
                "    \"id\": 1580417921,\n" +
                "    \"params\": {\n" +
                "        \"task_id\": %d,\n" +
                "        \"user_id\": %d,\n" +
                "        \"content\": \"%s\"\n" +
                "    }\n" +
                "}", taskid, userid, StringEscapeUtils.escapeJson(comment))});
    }

    @NonNull
    public static KanboardRequest updateComment(int commentid, @NonNull String comment) {
        return new KanboardRequest("updateComment", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"updateComment\",\n" +
                "    \"id\": 496470023,\n" +
                "    \"params\": {\n" +
                "        \"id\": %d,\n" +
                "        \"content\": \"%s\"\n" +
                "    }\n" +
                "}", commentid, StringEscapeUtils.escapeJson(comment))});
    }

    @NonNull
    public static KanboardRequest removeComment(int commentid) {
        return new KanboardRequest("removeComment", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"removeComment\",\n" +
                "    \"id\": 328836871,\n" +
                "    \"params\": {\n" +
                "        \"comment_id\": %d\n" +
                "    }\n" +
                "}", commentid)});
    }

    @NonNull
    public static KanboardRequest openTask(int taskid) {
        return new KanboardRequest("openTask", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"openTask\",\n" +
                "    \"id\": 1888531925,\n" +
                "    \"params\": {\n" +
                "        \"task_id\": %d\n" +
                "    }\n" +
                "}", taskid)});
    }

    @NonNull
    public static KanboardRequest closeTask(int taskid) {
        return new KanboardRequest("closeTask", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"closeTask\",\n" +
                "    \"id\": 1654396960,\n" +
                "    \"params\": {\n" +
                "        \"task_id\": %d\n" +
                "    }\n" +
                "}", taskid)});
    }

    @NonNull
    public static KanboardRequest removeTask(int taskid) {
        return new KanboardRequest("removeTask", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"removeTask\",\n" +
                "    \"id\": 1423501287,\n" +
                "    \"params\": {\n" +
                "        \"task_id\": %d\n" +
                "    }\n" +
                "}", taskid)});
    }

    @Nullable
    public static KanboardRequest createSubtask(int taskid, @NonNull String title, @Nullable Integer userid,
                                                @Nullable Integer timeestimated, @Nullable Integer timespent, @Nullable Integer status) {
        String content = String.format("" +
                "        \"task_id\": %d,\n" +
                "        \"title\": \"%s\"", taskid, StringEscapeUtils.escapeJson(title));
        if (userid != null)
            content += String.format("" + ",\n" +
                    "        \"user_id\": %d", userid);
        if (timeestimated != null)
            content += String.format("" + ",\n" +
                    "        \"user_id\": %d", timeestimated);
        if (timespent != null)
            content += String.format("" + ",\n" +
                    "        \"user_id\": %d", timespent);
        if (status != null)
            content += String.format("" + ",\n" +
                    "        \"user_id\": %d", status);
        return new KanboardRequest("createSubtask", new String[] {String.format(
                "{\n" +
                        "    \"jsonrpc\": \"2.0\",\n" +
                        "    \"method\": \"createSubtask\",\n" +
                        "    \"id\": 2041554661,\n" +
                        "    \"params\": {\n" +
                        "%s\n"+
                        "    }\n" +
                        "}", content)});
    }

    @NonNull
    public static KanboardRequest updateSubtask(int subtaskid, int taskid, @NonNull String title, @Nullable Integer userid,
                                                @Nullable Integer timeestimated, @Nullable Integer timespent, @Nullable Integer status) {
        String content = String.format("" +
                "        \"id\": %d,\n" +
                "        \"task_id\": %d", subtaskid, taskid);
        if (title != null)
            content += String.format("" + ",\n" +
                    "        \"title\": \"%s\"", StringEscapeUtils.escapeJson(title));
        if (userid != null)
            content += String.format("" + ",\n" +
                    "        \"user_id\": %d", userid);
        if (timeestimated != null)
            content += String.format("" + ",\n" +
                    "        \"user_id\": %d", timeestimated);
        if (timespent != null)
            content += String.format("" + ",\n" +
                    "        \"user_id\": %d", timespent);
        if (status != null)
            content += String.format("" + ",\n" +
                    "        \"user_id\": %d", status);
        return new KanboardRequest("updateSubtask", new String[] {String.format(
                "{\n" +
                        "    \"jsonrpc\": \"2.0\",\n" +
                        "    \"method\": \"updateSubtask\",\n" +
                        "    \"id\": 191749979,\n" +
                        "    \"params\": {\n" +
                        "%s\n"+
                        "    }\n" +
                        "}", content)});
    }

    @NonNull
    public static KanboardRequest getAllSubtasks(int taskid) {
        return new KanboardRequest("getAllSubtasks", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getAllSubtasks\",\n" +
                "    \"id\": 2087700490,\n" +
                "    \"params\": {\n" +
                "        \"task_id\":%d\n" +
                "    }\n" +
                "}", taskid)});
    }

    @NonNull
    public static KanboardRequest removeSubtask(int subtaskid) {
        return new KanboardRequest("removeSubtask", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"removeSubtask\",\n" +
                "    \"id\": 1382487306,\n" +
                "    \"params\": {\n" +
                "        \"subtask_id\": %d\n" +
                "    }\n" +
                "}", subtaskid)});
    }
    //endregion

    //region Custom API
    @NonNull
    public static KanboardRequest KD_getDashboard() {
        return new KanboardRequest("KD_getDashboard", new String[] {KanboardRequest.getMyDashboard().JSON[0],
                                                                    KanboardRequest.getMyOverdueTasks().JSON[0],
                                                                    KanboardRequest.getMyActivityStream().JSON[0]});
    }

    @NonNull
    public static KanboardRequest KD_getProjectById(int projectid) {
        return new KanboardRequest("KD_getProjectById", new String[] {KanboardRequest.getProjectById(projectid).JSON[0],
                                                                      KanboardRequest.getColumns(projectid).JSON[0],
                                                                      KanboardRequest.getActiveSwimlanes(projectid).JSON[0],
                                                                      KanboardRequest.getAllCategories(projectid).JSON[0],
                                                                      KanboardRequest.getAllTasks(projectid, 1).JSON[0],
                                                                      KanboardRequest.getAllTasks(projectid, 0).JSON[0],
                                                                      KanboardRequest.getOverdueTasksByProject(projectid).JSON[0],
                                                                      KanboardRequest.getProjectUsers(projectid).JSON[0]});
    }
    //endregion
}
