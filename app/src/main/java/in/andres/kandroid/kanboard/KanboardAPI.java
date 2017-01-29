package in.andres.kandroid.kanboard;


import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import in.andres.kandroid.kanboard.events.*;

public class KanboardAPI {

    private class KanboardAsync extends AsyncTask<KanboardRequest, Void, KanboardResult> {
        @Override
        protected KanboardResult doInBackground(KanboardRequest... params) {
            HttpsURLConnection con = null;
            List<JSONObject> responseList = new ArrayList<>();
            for (String s: params[0].JSON) {
                try {
                    Log.d("Send Request", s);
                    con = (HttpsURLConnection) kanboardURL.openConnection();
                    if (con == null)
                        return new KanboardResult(params[0], new JSONObject[]{new JSONObject("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":0,\"message\":\"Unable to open connection\"},\"id\":null}")}, 0);
                    con.setConnectTimeout(120000);
                    con.setReadTimeout(120000);
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setInstanceFollowRedirects(false);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("charset", "utf-8");
                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
                    out.writeBytes(s);
                    out.flush();
                    out.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    StringBuilder responseStr = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        responseStr.append(line);
                    }
                    in.close();

                    Log.d("Got Response", responseStr.toString());
                    JSONObject response;
                    try {
                        response = new JSONObject(responseStr.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        response = null;
                    }
                    responseList.add(response);
                } catch (SocketTimeoutException e) {
                    Log.e("JSON Request", "Connection timed out");
                    e.printStackTrace();
                    try {
                        responseList.add(new JSONObject("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":0,\"message\":\"Network Timeout\"},\"id\":null}"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("JSON Request", "Some Other Error");
                    e.printStackTrace();
                    try {
                        responseList.add(new JSONObject("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-1,\"message\":\"\" + e.getMessage() + \"\\\"},\\\"id\\\":null}\""));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            assert con != null;
            try {
                return new KanboardResult(params[0], responseList.toArray(new JSONObject[]{}), con.getResponseCode());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(KanboardResult s) {
            // Handle Errors
            if (s == null) {
                KanboardError res = new KanboardError(null, null, 0);
                for (KanbordEvents l: listeners)
                    l.onError(res);
                return;
            }
            if (s.Result[0].has("error") || s.ReturnCode >= 400) {
                JSONObject err = s.Result[0].optJSONObject("error");
                KanboardError res = new KanboardError(s.Request, err, s.ReturnCode);
                for (KanbordEvents l: listeners)
                    l.onError(res);
                return;
            }

            // Handle Return Messages
            boolean success = false;
            if (s.Request.Command.equalsIgnoreCase("getMe")) {
                KanboardUserInfo res = null;
                try {
                    if (s.Result[0].has("result") && (s.ReturnCode < 400)) {
                        success = true;
                        JSONObject jso = s.Result[0].getJSONObject("result");
                        res = new KanboardUserInfo(jso);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (KanbordEvents l: listeners)
                    l.onGetMe(success, res);
                for (OnGetMeListener l: onGetMeListeners)
                    l.onGetMe(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getMyProjectsList")) {
                List<KanboardProjectInfo> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new ArrayList<>();
                        JSONObject jso = s.Result[0].getJSONObject("result");
                        for (int i = 0; i < jso.names().length(); i++) {
                            String key = jso.names().getString(i);
                            res.add(new KanboardProjectInfo(Integer.parseInt(key), jso.optString(key, "")));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    for (KanbordEvents l: listeners)
                        l.onGetMyProjectsList(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getMyDashboard")) {
                KanboardDashboard res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        JSONObject dash = s.Result[0].getJSONObject("result");
                        res = new KanboardDashboard(dash);
                    }
                } catch (JSONException | MalformedURLException e) {
                    e.printStackTrace();
                }
                for (KanbordEvents l: listeners)
                    l.onGetMyDashboard(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getProjectUsers")) {
                Hashtable<Integer, String> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new Hashtable<>();
                        JSONObject jso = s.Result[0].getJSONObject("result");
                        Iterator<String> iter = jso.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            res.put(Integer.parseInt(key), jso.getString(key));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetProjectUsersListener l: onGetProjectUsersListeners)
                    l.onGetProjectUsers(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getTask")) {
                KanboardTask res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new KanboardTask(s.Result[0].getJSONObject("result"));
                    }
                } catch (JSONException | MalformedURLException e) {
                    e.printStackTrace();
                }
                for (OnGetTaskListener l: onGetTaskListeners)
                    l.onGetTask(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("openTask")) {
                try {
                    success = s.Result[0].getBoolean("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnOpenTaskListener l: onOpenTaskListeners)
                    l.onOpenTask(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("closeTask")) {
                try {
                    success = s.Result[0].getBoolean("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnCloseTaskListener l: onCloseTaskListeners)
                    l.onCloseTask(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("removeTask")) {
                try {
                    success = s.Result[0].getBoolean("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnRemoveTaskListener l: onRemoveTaskListeners)
                    l.onRemoveTask(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getDefaultSwimlane")) {
                String res = null;
                boolean active = false;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        JSONObject jso = s.Result[0].getJSONObject("result");
                        res = jso.getString("default_swimlane");
                        active = KanboardAPI.StringToBoolean(jso.optString("show_default_swimlane"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetDefaultSwimlaneListener l: onGetDefaultSwimlaneListeners)
                    l.onGetDefaultSwimlane(success, res, active);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getSwimlane")) {
                KanboardSwimlane res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new KanboardSwimlane(s.Result[0].getJSONObject("result"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetSwimlaneListener l: onGetSwimlaneListeners)
                    l.onGetSwimlane(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getCategory")) {
                KanboardCategory res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new KanboardCategory(s.Result[0].getJSONObject("result"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetCategoryListener l: onGetCategoryListeners)
                    l.onGetCategory(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("createComment")) {
                Integer res = null;
                try {
                    if (!s.Result[0].getString("result").equalsIgnoreCase("false")) {
                        success = true;
                        res = s.Result[0].getInt("result");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnCreateCommentListener l: onCreateCommentListeners)
                    l.onCreateComment(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getAllComments")) {
                List<KanboardComment> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new ArrayList<>();
                        JSONArray jsa = s.Result[0].getJSONArray("result");
                        for (int i = 0; i < jsa.length(); i++)
                            res.add(new KanboardComment(jsa.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetAllCommentsListener l: onGetAllCommentsListeners)
                    l.onGetAllComments(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("updateComment")) {
                try {
                    success = s.Result[0].getBoolean("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnUpdateCommentListener l: onUpdateCommentListeners)
                    l.onUpdateComment(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("removeComment")) {
                try {
                    success = s.Result[0].getBoolean("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnRemoveCommentListener l: onRemoveCommentListeners)
                    l.onRemoveComment(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("createSubtask")) {
                Integer res = null;
                try {
                    if (!s.Result[0].getString("result").equalsIgnoreCase("false")) {
                        success = true;
                        res = s.Result[0].getInt("result");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnCreateSubtaskListener l: onCreateSubtaskListeners)
                    l.onCreateSubtask(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getAllSubtasks")) {
                List<KanboardSubtask> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new ArrayList<>();
                        JSONArray jsa = s.Result[0].getJSONArray("result");
                        for (int i = 0; i < jsa.length(); i++)
                            res.add(new KanboardSubtask(jsa.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetAllSubtasksListener l: onGetAllSubtasksListeners)
                    l.onGetAllSubtasks(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("updateSubtask")) {
                try {
                    success = s.Result[0].getBoolean("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnUpdateSubtaskListener l: onUpdateSubtaskListeners)
                    l.onUpdateSubtask(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("removeSubtask")) {
                try {
                    success = s.Result[0].getBoolean("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnRemoveSubtaskListener l: onRemoveSubtaskListeners)
                    l.onRemoveSubtask(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("KD_getDashboard")) {
                KanboardDashboard res = null;
                try {
                    if (s.Result[0].has("result") && s.Result[1].has("result") && s.Result[2].has("result")) {
                        success = true;
                        res = new KanboardDashboard(s.Result[0].getJSONObject("result"), s.Result[1].getJSONArray("result"), s.Result[2].getJSONArray("result"));
                    }
                } catch (JSONException | MalformedURLException e) {
                    e.printStackTrace();
                }
                for (KanbordEvents l: listeners)
                    l.onGetMyDashboard(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("KD_getProjectById")) {
                KanboardProject res = null;
                try {
                    if (s.Result[0].has("result") && s.Result[1].has("result") && s.Result[2].has("result") && s.Result[3].has("result") && s.Result[4].has("result") && s.Result[5].has("result") && s.Result[6].has("result")) {
                        success = true;
                        res = new KanboardProject(s.Result[0].optJSONObject("result"),
                                                  s.Result[1].optJSONArray("result"),
                                                  s.Result[2].optJSONArray("result"),
                                                  s.Result[3].optJSONArray("result"),
                                                  s.Result[4].optJSONArray("result"),
                                                  s.Result[5].optJSONArray("result"),
                                                  s.Result[6].optJSONArray("result"),
                                                  s.Result[7].optJSONObject("result"));
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                for (KanbordEvents l: listeners)
                    l.onGetProjectById(success, res);
                return;
            }
        }
    }

    private URL kanboardURL;
    private HashSet<KanbordEvents> listeners = new HashSet<>();
    private HashSet<OnGetAllCommentsListener> onGetAllCommentsListeners = new HashSet<>();
    private HashSet<OnGetTaskListener> onGetTaskListeners = new HashSet<>();
    private HashSet<OnGetSwimlaneListener> onGetSwimlaneListeners = new HashSet<>();
    private HashSet<OnGetCategoryListener> onGetCategoryListeners = new HashSet<>();
    private HashSet<OnGetProjectUsersListener> onGetProjectUsersListeners = new HashSet<>();
    private HashSet<OnGetDefaultSwimlaneListener> onGetDefaultSwimlaneListeners = new HashSet<>();
    private HashSet<OnGetAllSubtasksListener> onGetAllSubtasksListeners = new HashSet<>();
    private HashSet<OnGetMeListener> onGetMeListeners = new HashSet<>();
    private HashSet<OnCreateCommentListener> onCreateCommentListeners = new HashSet<>();
    private HashSet<OnUpdateCommentListener> onUpdateCommentListeners = new HashSet<>();
    private HashSet<OnRemoveCommentListener> onRemoveCommentListeners = new HashSet<>();
    private HashSet<OnCreateSubtaskListener> onCreateSubtaskListeners = new HashSet<>();
    private HashSet<OnUpdateSubtaskListener> onUpdateSubtaskListeners = new HashSet<>();
    private HashSet<OnRemoveSubtaskListener> onRemoveSubtaskListeners = new HashSet<>();
    private HashSet<OnOpenTaskListener> onOpenTaskListeners = new HashSet<>();
    private HashSet<OnCloseTaskListener> onCloseTaskListeners = new HashSet<>();
    private HashSet<OnRemoveTaskListener> onRemoveTaskListeners = new HashSet<>();

    public KanboardAPI(String serverURL, final String username, final String password) throws IOException {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }

        });
        String tmpURL = serverURL;
        if (!serverURL.endsWith("jsonrpc.php")) {
            if (!serverURL.endsWith("/"))
                tmpURL += "/";
            tmpURL += "jsonrpc.php";
        }
        kanboardURL = new URL(tmpURL);
    }

    public void addListener(@NonNull KanbordEvents listener) {
        listeners.add(listener);
    }

    public void removeListener(@NonNull KanbordEvents listener) {
        listeners.remove(listener);
    }

    public void addOnGetAllCommentsListener(@NonNull OnGetAllCommentsListener listener) {
        onGetAllCommentsListeners.add(listener);
    }

    public void removeOnGetAllCommentsListener(@NonNull OnGetAllCommentsListener listener) {
        onGetAllCommentsListeners.remove(listener);
    }

    public void addOnGetSwimlaneListener(@NonNull OnGetSwimlaneListener listener) {
        onGetSwimlaneListeners.add(listener);
    }

    public void removeOnGetSwimlaneListener(@NonNull OnGetSwimlaneListener listener) {
        onGetSwimlaneListeners.remove(listener);
    }

    public void addOnGetDefaultSwimlaneListener(@NonNull OnGetDefaultSwimlaneListener listener) {
        onGetDefaultSwimlaneListeners.add(listener);
    }

    public void removeOnGetDefaultSwimlaneListener(@NonNull OnGetDefaultSwimlaneListener listener) {
        onGetDefaultSwimlaneListeners.remove(listener);
    }

    public void addOnGetTaskListener(@NonNull OnGetTaskListener listener) {
        onGetTaskListeners.add(listener);
    }

    public void removeGetTaskListener(@NonNull OnGetTaskListener listener) {
        onGetTaskListeners.remove(listener);
    }

    public void addOnOpenTaskListener(@NonNull OnOpenTaskListener listener) {
        onOpenTaskListeners.add(listener);
    }

    public void removeOnOpenTaskListener(@NonNull OnOpenTaskListener listener) {
        onOpenTaskListeners.remove(listener);
    }

    public void addOnCloseTaskListener(@NonNull OnCloseTaskListener listener) {
        onCloseTaskListeners.add(listener);
    }

    public void removeOnCloseTaskListener(@NonNull OnCloseTaskListener listener) {
        onCloseTaskListeners.remove(listener);
    }

    public void addOnRemoveTaskListener(@NonNull OnRemoveTaskListener listener) {
        onRemoveTaskListeners.add(listener);
    }

    public void removeOnRemoveTaskListener(@NonNull OnRemoveTaskListener listener) {
        onRemoveTaskListeners.remove(listener);
    }

    public void addOnGetCategoryListener(@NonNull OnGetCategoryListener listener) {
        onGetCategoryListeners.add(listener);
    }

    public void removeOnGetCategoryListener(@NonNull OnGetCategoryListener listener) {
        onGetCategoryListeners.remove(listener);
    }

    public void addOnGetProjectUsersListener(@NonNull OnGetProjectUsersListener listener) {
        onGetProjectUsersListeners.add(listener);
    }

    public void removeOnGetProjectUsersListener(@NonNull OnGetProjectUsersListener listener) {
        onGetProjectUsersListeners.remove(listener);
    }

    public void addOnGetAllSubtasksListener(@NonNull OnGetAllSubtasksListener listener) {
        onGetAllSubtasksListeners.add(listener);
    }

    public void removeOnGetAllSubtasksListener(@NonNull OnGetAllSubtasksListener listener) {
        onGetAllSubtasksListeners.remove(listener);
    }

    public void addOnGetMeListener(@NonNull OnGetMeListener listener) {
        onGetMeListeners.add(listener);
    }

    public void removeOnGetMeListener(@NonNull OnGetMeListener listener) {
        onGetMeListeners.remove(listener);
    }

    public void addOnCreateCommentListener(@NonNull OnCreateCommentListener listener) {
        onCreateCommentListeners.add(listener);
    }

    public void removeOnCreateCommentListener(@NonNull OnCreateCommentListener listener) {
        onCreateCommentListeners.remove(listener);
    }

    public void addOnUpdateCommentListener(@NonNull OnUpdateCommentListener listener) {
        onUpdateCommentListeners.add(listener);
    }

    public void removeOnUpdateCommentListener(@NonNull OnUpdateCommentListener listener) {
        onUpdateCommentListeners.remove(listener);
    }

    public void addOnRemoveCommentListener(@NonNull OnRemoveCommentListener listener) {
        onRemoveCommentListeners.add(listener);
    }

    public void removeOnRemoveCommentListener(@NonNull OnRemoveCommentListener listener) {
        onRemoveCommentListeners.remove(listener);
    }

    public void addOnCreateSubtaskListener(@NonNull OnCreateSubtaskListener listener) {
        onCreateSubtaskListeners.add(listener);
    }

    public void removeOnCreateSubtaskListener(@NonNull OnCreateSubtaskListener listener) {
        onCreateSubtaskListeners.remove(listener);
    }

    public void addOnUpdateSubtaskListener(@NonNull OnUpdateSubtaskListener listener) {
        onUpdateSubtaskListeners.add(listener);
    }

    public void removeOnUpdateSubtaskListener(@NonNull OnUpdateSubtaskListener listener) {
        onUpdateSubtaskListeners.remove(listener);
    }

    public void addOnRemoveSubtaskListener(@NonNull OnRemoveSubtaskListener listener) {
        onRemoveSubtaskListeners.add(listener);
    }

    public void removeOnRemoveSubtaskListener(@NonNull OnRemoveSubtaskListener listener) {
        onRemoveSubtaskListeners.remove(listener);
    }

    public void getMe() {
        new KanboardAsync().execute(KanboardRequest.getMe());
    }

    public void getMyProjectsList() {
        new KanboardAsync().execute(KanboardRequest.getMyProjectsList());
    }

    public void getMyDashboard() {
        new KanboardAsync().execute(KanboardRequest.getMyDashboard());
    }

    public void getProjectUsers(int projectid) {
        new KanboardAsync().execute(KanboardRequest.getProjectUsers(projectid));
    }

    public void getTask(int taskid) {
        new KanboardAsync().execute(KanboardRequest.getTask(taskid));
    }

    public void openTask(int taskid) {
        new KanboardAsync().execute(KanboardRequest.openTask(taskid));
    }

    public void closeTask(int taskid) {
        new KanboardAsync().execute(KanboardRequest.closeTask(taskid));
    }

    public void removeTask(int taskid) {
        new KanboardAsync().execute(KanboardRequest.removeTask(taskid));
    }

    public void getAllComments(int taskid) {
        new KanboardAsync().execute(KanboardRequest.getAllComments(taskid));
    }

    public void createComment(int taskid, int userid, String comment) {
        new KanboardAsync().execute(KanboardRequest.createComment(taskid, userid, comment));
    }

    public void updateComment(int commentid, @NonNull String comment) {
        new KanboardAsync().execute(KanboardRequest.updateComment(commentid, comment));
    }

    public void removeComment(int commentid) {
        new KanboardAsync().execute(KanboardRequest.removeComment(commentid));
    }

    public void getDefaultSwimlane(int projectid) {
        new KanboardAsync().execute(KanboardRequest.getDefaultSwimlane(projectid));
    }

    public void getSwimlane(int swimlaneid) {
        new KanboardAsync().execute(KanboardRequest.getSwimlane(swimlaneid));
    }

    public void getCategory(int categoryid) {
        new KanboardAsync().execute(KanboardRequest.getCategrory(categoryid));
    }

    public void getAllSubtasks(int taskid) {
        new KanboardAsync().execute(KanboardRequest.getAllSubtasks(taskid));
    }

    public void createSubtask(int taskid, @NonNull String title, @Nullable Integer userid,
                              @Nullable Integer timeestimated, @Nullable Integer timespent, @Nullable Integer status) {
        new KanboardAsync().execute(KanboardRequest.createSubtask(taskid, title, userid, timeestimated, timespent, status));
    }

    public void updateSubtask(int subtaskid, int taskid, @NonNull String title, @Nullable Integer userid,
                              @Nullable Integer timeestimated, @Nullable Integer timespent, @Nullable Integer status) {
        new KanboardAsync().execute(KanboardRequest.updateSubtask(subtaskid, taskid, title, userid, timeestimated, timespent, status));
    }

    public void removeSubtask(int subtaskid) {
        new KanboardAsync().execute(KanboardRequest.removeSubtask(subtaskid));
    }

    public void KB_getDashboard() {
        new KanboardAsync().execute(KanboardRequest.KD_getDashboard());
    }

    public void KB_getProjectById(int projectid) {
        new KanboardAsync().execute(KanboardRequest.KD_getProjectById(projectid));
    }

    // TODO: add API calls

    public static boolean StringToBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("1");
    }

    @Nullable
    public static Integer parseColorString(String colorstring) {
        try {
            return Color.parseColor(colorstring);
        } catch (IllegalArgumentException e) {
            Pattern c = Pattern.compile("rgb *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)");
            Matcher m = c.matcher(colorstring);

            if (m.matches())
            {
                return Color.rgb(Integer.valueOf(m.group(1)),  // r
                        Integer.valueOf(m.group(2)),  // g
                        Integer.valueOf(m.group(3))); // b
            }

        }
        return null;
    }
}
