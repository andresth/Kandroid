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

//import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Date;

import in.andres.kandroid.kanboard.events.OnSubtaskTimetrackingListener;

@SuppressWarnings("unused")
public class KanboardRequest {
    public final String Command;
    public final String[] JSON;
    public final Object Listener;

    private KanboardRequest(@NonNull String command, @NonNull String[] json) {
        this(command, json, null);
    }

    private KanboardRequest(@NonNull String command, @NonNull String[] json, @Nullable Object listener) {
        this.Command = command;
        this.JSON = json;
        this.Listener = listener;
    }

    //region Kanboard API
    @NonNull
    public static KanboardRequest getMe() {
        return new KanboardRequest("getMe", new String[] {"{\"jsonrpc\": \"2.0\", \"method\": \"getMe\", \"id\": 1}"});
    }

    public static KanboardRequest getVersion() {
        return new KanboardRequest("getVersion", new String[] {"{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getVersion\",\n" +
                "    \"id\": 1661138292\n" +
                "}"});
    }

    public static KanboardRequest getDefaultTaskColors() {
        return new KanboardRequest("getDefaultTaskColors", new String[] {"{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getDefaultTaskColors\",\n" +
                "    \"id\": 2108929212\n" +
                "}"});
    }

    public static KanboardRequest getDefaultTaskColor() {
        return new KanboardRequest("getDefaultTaskColor", new String[] {"{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getDefaultTaskColor\",\n" +
                "    \"id\": 1144775215\n" +
                "}"});
    }

    @NonNull
    public static KanboardRequest getMyProjectsList() {
        return new KanboardRequest("getMyProjectsList", new String[] {"{\"jsonrpc\": \"2.0\", \"method\": \"getMyProjectsList\", \"id\": 1}"});
    }

    @NonNull
    public static KanboardRequest getMyProjects() {
        return new KanboardRequest("getMyProjects", new String[] {"{\"jsonrpc\": \"2.0\", \"method\": \"getMyProjects\", \"id\": 2134420212}"});
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
    public static KanboardRequest createTask(@NonNull String title, int projectid, @Nullable String colorid,
                                             @Nullable Integer columnid, @Nullable Integer ownerid,
                                             @Nullable Integer creatorid, @Nullable Date duedate,
                                             @Nullable String description, @Nullable Integer categoryid,
                                             @Nullable Integer score, @Nullable Integer swimlaneid,
                                             @Nullable Integer priority, @Nullable Integer recurrencestatus,
                                             @Nullable Integer recurrencetrigger, @Nullable Integer recurrencefactor,
                                             @Nullable Integer recurrencetimeframe, @Nullable Integer recurrencebasedate,
                                             @Nullable String[] tags, @Nullable Date starteddate) {
        String content = String.format("" +
                "   \"title\": \"%s\",\n" +
                "   \"project_id\": %d", StringEscapeUtils.escapeJson(title), projectid);
        if (colorid != null)
            content += String.format(", \n \"color_id\": \"%s\"", colorid);
        if (columnid != null)
            content += String.format(", \n \"column_id\": %d", columnid);
        if (ownerid != null)
            content += String.format(", \n \"owner_id\": %d", ownerid);
        if (creatorid != null)
            content += String.format(", \n \"creator_id\": %d", creatorid);
        if (duedate != null)
            content += String.format(", \n \"date_due\": \"%tF\"", duedate);
        if (description != null)
            content += String.format(", \n \"description\": \"%s\"", StringEscapeUtils.escapeJson(description));
        if (categoryid != null)
            content += String.format(", \n \"category_id\": %d", categoryid);
        if (score != null)
            content += String.format(", \n \"score\": %d", score);
        if (swimlaneid != null)
            content += String.format(", \n \"swimlane_id\": %d", swimlaneid);
        if (priority != null)
            content += String.format(", \n \"priority\": %d", priority);
        if (recurrencestatus != null)
            content += String.format(", \n \"recurrence_status\": %d", recurrencestatus);
        if (recurrencetrigger != null)
            content += String.format(", \n \"recurrence_trigger\": %d", recurrencetrigger);
        if (recurrencefactor != null)
            content += String.format(", \n \"recurrence_factor\": %d", recurrencefactor);
        if (recurrencetimeframe != null)
            content += String.format(", \n \"recurrence_timeframe\": %d", recurrencetimeframe);
        if (recurrencebasedate != null)
            content += String.format(", \n \"recurrence_basedate\": %d", recurrencebasedate);
        if (starteddate != null)
            content += String.format(", \n \"date_started\": \"%1$tF %1$tH:%1$tM\"", starteddate);
        //TODO: Add tags
        return new KanboardRequest("createTask", new String[] {String.format(
                "{\n" +
                        "    \"jsonrpc\": \"2.0\",\n" +
                        "    \"method\": \"createTask\",\n" +
                        "    \"id\": 1406803059,\n" +
                        "    \"params\": {\n" +
                        "%s\n" +
                        "    }\n" +
                        "}", content)});
    }

    @NonNull
    public static KanboardRequest updateTask(int taskid, @NonNull String title, @Nullable String colorid,
                                             @Nullable Integer ownerid,
                                             @Nullable Date duedate,
                                             @Nullable String description, @Nullable Integer categoryid,
                                             @Nullable Integer score,
                                             @Nullable Integer priority, @Nullable Integer recurrencestatus,
                                             @Nullable Integer recurrencetrigger, @Nullable Integer recurrencefactor,
                                             @Nullable Integer recurrencetimeframe, @Nullable Integer recurrencebasedate,
                                             @Nullable String[] tags, @Nullable Date starteddate) {
        String content = String.format("" +
                "   \"title\": \"%s\",\n" +
                "   \"id\": %d", StringEscapeUtils.escapeJson(title), taskid);
        if (colorid != null)
            content += String.format(", \n \"color_id\": \"%s\"", colorid);
        if (ownerid != null)
            content += String.format(", \n \"owner_id\": %d", ownerid);
        if (duedate != null)
            content += String.format(", \n \"date_due\": \"%tF\"", duedate);
        else
            content += ", \n \"date_due\": \"\"";
        if (description != null)
            content += String.format(", \n \"description\": \"%s\"", StringEscapeUtils.escapeJson(description));
        if (categoryid != null)
            content += String.format(", \n \"category_id\": %d", categoryid);
        if (score != null)
            content += String.format(", \n \"score\": %d", score);
        if (priority != null)
            content += String.format(", \n \"priority\": %d", priority);
        if (recurrencestatus != null)
            content += String.format(", \n \"recurrence_status\": %d", recurrencestatus);
        if (recurrencetrigger != null)
            content += String.format(", \n \"recurrence_trigger\": %d", recurrencetrigger);
        if (recurrencefactor != null)
            content += String.format(", \n \"recurrence_factor\": %d", recurrencefactor);
        if (recurrencetimeframe != null)
            content += String.format(", \n \"recurrence_timeframe\": %d", recurrencetimeframe);
        if (recurrencebasedate != null)
            content += String.format(", \n \"recurrence_basedate\": %d", recurrencebasedate);
        if (starteddate != null)
            content += String.format(", \n \"date_started\": \"%1$tF %1$tH:%1$tM\"", starteddate);
        else
            content += ", \n \"date_started\": \"\"";
        //TODO: Add tags
        return new KanboardRequest("updateTask", new String[] {String.format(
                "{\n" +
                        "    \"jsonrpc\": \"2.0\",\n" +
                        "    \"method\": \"updateTask\",\n" +
                        "    \"id\": 1406803059,\n" +
                        "    \"params\": {\n" +
                        "%s\n" +
                        "    }\n" +
                        "}", content)});
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

    @NonNull
    public static KanboardRequest moveTaskPosition(int projectid, int taskid, int columnid, int position,
                                                   int swimlaneid) {
        return new KanboardRequest("moveTaskPosition", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"moveTaskPosition\",\n" +
                "    \"id\": 117211800,\n" +
                "    \"params\": {\n" +
                "        \"project_id\": %d,\n" +
                "        \"task_id\": %d,\n" +
                "        \"column_id\": %d,\n" +
                "        \"position\": %d,\n" +
                "        \"swimlane_id\": %d\n" +
                "    }\n" +
                "}", projectid, taskid, columnid, position, swimlaneid)});
    }

    @NonNull
    public static KanboardRequest moveTaskToProject(int projectid, int taskid, @Nullable Integer columnid,
                                                    @Nullable Integer position, @Nullable Integer swimlaneid) {
        String content = String.format("" +
                "        \"project_id\": %d,\n" +
                "        \"task_id\": %d", projectid, taskid);
        if (columnid != null)
            content += String.format("" + ",\n        \"column_id\": %d", columnid);
        if (position != null)
            content += String.format("" + ",\n        \"position\": %d", position);
        if (swimlaneid != null)
            content += String.format("" + ",\n        \"swimlane_id\": %d", swimlaneid);
        return new KanboardRequest("moveTaskToProject", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"moveTaskToProject\",\n" +
                "    \"id\": 15775829,\n" +
                "    \"params\": {\n" +
                "%s\n" +
                "    }\n" +
                "}", content)});
    }

    @NonNull
    public static KanboardRequest duplicateTaskToProject(int projectid, int taskid, @Nullable Integer columnid,
                                                    @Nullable Integer position, @Nullable Integer swimlaneid) {
        String content = String.format("" +
                "        \"project_id\": %d,\n" +
                "        \"task_id\": %d", projectid, taskid);
        if (columnid != null)
            content += String.format("" + ",\n        \"column_id\": %d", columnid);
        if (position != null)
            content += String.format("" + ",\n        \"position\": %d", position);
        if (swimlaneid != null)
            content += String.format("" + ",\n        \"swimlane_id\": %d", swimlaneid);
        return new KanboardRequest("duplicateTaskToProject", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"duplicateTaskToProject\",\n" +
                "    \"id\": 1662458687,\n" +
                "    \"params\": {\n" +
                "%s\n" +
                "    }\n" +
                "}", content)});
    }

    @NonNull
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
                    "        \"time_estimated\": %d", timeestimated);
        if (timespent != null)
            content += String.format("" + ",\n" +
                    "        \"time_spent\": %d", timespent);
        if (status != null)
            content += String.format("" + ",\n" +
                    "        \"status\": %d", status);
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
    public static KanboardRequest updateSubtask(int subtaskid, int taskid, @Nullable String title, @Nullable Integer userid,
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
                    "        \"time_estimated\": %d", timeestimated);
        if (timespent != null)
            content += String.format("" + ",\n" +
                    "        \"time_spent\": %d", timespent);
        if (status != null)
            content += String.format("" + ",\n" +
                    "        \"status\": %d", status);
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

    @NonNull
    public static KanboardRequest hasSubtaskTimer(int subtaskid, int userid, @NonNull OnSubtaskTimetrackingListener listener) {
        return new KanboardRequest("hasSubtaskTimer", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"hasSubtaskTimer\"," +
                "    \"id\": 1786995697,\n" +
                "    \"params\": [%d, %d]\n" +
                "}", subtaskid, userid)}, listener);
    }

    @NonNull
    public static KanboardRequest setSubtaskStartTime(int subtaskid, int userid, @NonNull OnSubtaskTimetrackingListener listener) {
        return new KanboardRequest("setSubtaskStartTime", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"setSubtaskStartTime\"," +
                "    \"id\": 1168991769,\n" +
                "    \"params\": [%d, %d]\n" +
                "}", subtaskid, userid)}, listener);
    }

    @NonNull
    public static KanboardRequest setSubtaskEndTime(int subtaskid, int userid, @NonNull OnSubtaskTimetrackingListener listener) {
        return new KanboardRequest("setSubtaskEndTime", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"setSubtaskEndTime\"," +
                "    \"id\": 1026607603,\n" +
                "    \"params\": [%d, %d]\n" +
                "}", subtaskid, userid)}, listener);
    }

    @NonNull
    public static KanboardRequest getSubtaskTimeSpent(int subtaskid, int userid, @NonNull OnSubtaskTimetrackingListener listener) {
        return new KanboardRequest("getSubtaskTimeSpent", new String[] {String.format(
                "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"method\": \"getSubtaskTimeSpent\"," +
                "    \"id\": 738527378,\n" +
                "    \"params\": [%d, %d]\n" +
                "}", subtaskid, userid)}, listener);
    }

    @NonNull
    public static KanboardRequest getAllTaskFiles(int taskid) {
        return new KanboardRequest("getAllTaskFiles", new String[] {String.format(
                "{\n" +
                        "    \"jsonrpc\": \"2.0\",\n" +
                        "    \"method\": \"getAllTaskFiles\",\n" +
                        "    \"id\": 1880662820,\n" +
                        "    \"params\": {\n" +
                        "        \"task_id\": %d\n" +
                        "    }\n" +
                        "}", taskid)});
    }

    @NonNull
    public static KanboardRequest removeTaskFile(int fileid) {
        return new KanboardRequest("removeTaskFile", new String[] {String.format(
                "{\n" +
                        "    \"jsonrpc\": \"2.0\",\n" +
                        "    \"method\": \"removeTaskFile\",\n" +
                        "    \"id\": 447036524,\n" +
                        "    \"params\": [\n" +
                        "        \"%d\"\n" +
                        "    ]" +
                        "}", fileid)});
    }

    @NonNull
    public static KanboardRequest downloadTaskFile(int fileid) {
        return new KanboardRequest("downloadTaskFile", new String[] {String.format(
                "{\n" +
                        "    \"jsonrpc\": \"2.0\",\n" +
                        "    \"method\": \"downloadTaskFile\",\n" +
                        "    \"id\": %1$d,\n" +
                        "    \"params\": [\n" +
                        "        \"%1$d\"\n" +
                        "    ]" +
                        "}", fileid)});
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
