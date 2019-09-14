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


import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import in.andres.kandroid.BuildConfig;
import in.andres.kandroid.Constants;
import in.andres.kandroid.kanboard.events.OnCloseTaskListener;
import in.andres.kandroid.kanboard.events.OnCreateCommentListener;
import in.andres.kandroid.kanboard.events.OnCreateSubtaskListener;
import in.andres.kandroid.kanboard.events.OnCreateTaskListener;
import in.andres.kandroid.kanboard.events.OnDownloadTaskFileListener;
import in.andres.kandroid.kanboard.events.OnDuplicateTaskToProjectListener;
import in.andres.kandroid.kanboard.events.OnErrorListener;
import in.andres.kandroid.kanboard.events.OnGetActiveSwimlanesListener;
import in.andres.kandroid.kanboard.events.OnGetAllCategoriesListener;
import in.andres.kandroid.kanboard.events.OnGetAllCommentsListener;
import in.andres.kandroid.kanboard.events.OnGetAllSubtasksListener;
import in.andres.kandroid.kanboard.events.OnGetAllTaskFilesListener;
import in.andres.kandroid.kanboard.events.OnGetAllTasksListener;
import in.andres.kandroid.kanboard.events.OnGetCategoryListener;
import in.andres.kandroid.kanboard.events.OnGetColumnsListener;
import in.andres.kandroid.kanboard.events.OnGetDefaultColorListener;
import in.andres.kandroid.kanboard.events.OnGetDefaultColorsListener;
import in.andres.kandroid.kanboard.events.OnGetMeListener;
import in.andres.kandroid.kanboard.events.OnGetMyActivityStreamListener;
import in.andres.kandroid.kanboard.events.OnGetMyDashboardListener;
import in.andres.kandroid.kanboard.events.OnGetMyOverdueTasksListener;
import in.andres.kandroid.kanboard.events.OnGetMyProjectsListListener;
import in.andres.kandroid.kanboard.events.OnGetMyProjectsListener;
import in.andres.kandroid.kanboard.events.OnGetOverdueTasksByProjectListener;
import in.andres.kandroid.kanboard.events.OnGetProjectByIdListener;
import in.andres.kandroid.kanboard.events.OnGetProjectUsersListener;
import in.andres.kandroid.kanboard.events.OnGetSwimlaneListener;
import in.andres.kandroid.kanboard.events.OnGetTaskListener;
import in.andres.kandroid.kanboard.events.OnGetVersionListener;
import in.andres.kandroid.kanboard.events.OnMoveTaskPositionListener;
import in.andres.kandroid.kanboard.events.OnMoveTaskToProjectListener;
import in.andres.kandroid.kanboard.events.OnOpenTaskListener;
import in.andres.kandroid.kanboard.events.OnRemoveCommentListener;
import in.andres.kandroid.kanboard.events.OnRemoveSubtaskListener;
import in.andres.kandroid.kanboard.events.OnRemoveTaskFileListener;
import in.andres.kandroid.kanboard.events.OnRemoveTaskListener;
import in.andres.kandroid.kanboard.events.OnSubtaskTimetrackingListener;
import in.andres.kandroid.kanboard.events.OnUpdateCommentListener;
import in.andres.kandroid.kanboard.events.OnUpdateSubtaskListener;
import in.andres.kandroid.kanboard.events.OnUpdateTaskListener;

@SuppressWarnings("unused")
public class KanboardAPI {

    public static HttpURLConnection openConnection(URL url, String data) throws IOException, CertificateException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        SSLContext sslcontext = null;
        HttpURLConnection con = null;
        URL tmpURL = url;

        do {
            con = (HttpURLConnection) tmpURL.openConnection();
            if (con == null)
                return null;

            if (url.getProtocol().equalsIgnoreCase("https")) {
                KeyStore keyStore = KeyStore.getInstance("AndroidCAStore");
                keyStore.load(null);
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);
                sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, tmf.getTrustManagers(), null);
                ((HttpsURLConnection) con).setSSLSocketFactory(sslcontext.getSocketFactory());
            }
            con.setConnectTimeout(120000);
            con.setReadTimeout(120000);
            con.setInstanceFollowRedirects(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("charset", "utf-8");
            con.setDoOutput(true);
            con.setDoInput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(data);
            out.flush();
            out.close();

            Log.d(Constants.TAG, String.format("API: Connected to %s", con.getURL().toString()));

            if (con.getResponseCode() == 301 || con.getResponseCode() == 302 || con.getResponseCode() == 307 || con.getResponseCode() == 308) {
                Log.i(Constants.TAG, "Follow URL redirect.");
                tmpURL = new URL(con.getHeaderField("Location"));
                Log.d(Constants.TAG, String.format("API: Redirect to %s", tmpURL.toString()));
                con.disconnect();
            } else {
                break;
            }
        } while (true);
        return con;
    }

    private static Pattern urlPattern = Pattern.compile("(?i)[^/]+?\\.(php|html|htm|asp){1}?$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public static URL sanitizeURL(String url) throws MalformedURLException {
        Matcher regexMatcher = urlPattern.matcher(url);
        if (regexMatcher.find()) {
            //URL has filename, replace it
            return new URL(regexMatcher.replaceAll("jsonrpc.php"));
        } else {
            //URL hs no filename, add one
            if (!url.endsWith("/"))
                url += "/";
            url += "jsonrpc.php";
            return new URL(url);
        }
    }

    private class KanboardAsync extends AsyncTask<KanboardRequest, Void, KanboardResult> {
        @Override
        protected KanboardResult doInBackground(KanboardRequest... params) {
            HttpURLConnection con = null;
            SSLContext sslcontext = null;
            int httpResponseCode = 0;
            List<JSONObject> responseList = new ArrayList<>();
            for (String s: params[0].JSON) {
                try {

                    Log.i(Constants.TAG, String.format("API: Send Request \"%s\"", params[0].Command));
                    if (BuildConfig.DEBUG) Log.v(Constants.TAG, String.format("API: Data:\n%s", s));
                    con = KanboardAPI.openConnection(kanboardURL, s);
                    if (con == null)
                        return new KanboardResult(params[0], new JSONObject[]{new JSONObject("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":0,\"message\":\"Unable to open connection\"},\"id\":null}")}, 0);

                    Log.i(Constants.TAG, String.format("API: HTTP Return Code for \"%s\": %d", params[0].Command, con.getResponseCode()));


                    BufferedReader in;
                    if (con.getResponseCode() < 400)
                        in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    else
                        in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String line;
                    StringBuilder responseStr = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        responseStr.append(line);
                    }
                    in.close();
                    httpResponseCode = con.getResponseCode();

                    Log.i(Constants.TAG, String.format("API: Received Response \"%s\"", params[0].Command));
                    if (BuildConfig.DEBUG)
                        Log.v(Constants.TAG, String.format("API: Data:\n%s", responseStr.toString()));
                    JSONObject response;
                    try {
                        response = new JSONObject(responseStr.toString());
                    } catch (JSONException e) {
                        //server response is not a JSON string, print it for debugging
                        Log.d(Constants.TAG, String.format(Locale.getDefault(), "Erroneous response: %s", responseStr.toString()));
                        response = new JSONObject();
                        response.put("jsonrpc", "2.0");
                        response.put("id", null);
                        response.put("error", new JSONObject()
                                .put("code", -50)
                                .put("responseCode", con.getResponseCode())
                                .put("message", con.getResponseMessage()))
                                .put("data", responseStr.toString());
                    }
                    responseList.add(response);

                    con.disconnect();

                } catch (SSLException e) {
                    Log.e(Constants.TAG, "API: SSL Error.");
                    e.printStackTrace();
                    try {
                        responseList.add(new JSONObject("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-40,\"message\":\"" + e.getLocalizedMessage() + "\"},\"id\":null}"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } catch (UnknownHostException e) {
                    Log.e(Constants.TAG, "API: Unknown Host.");
                    e.printStackTrace();
                    try {
                        responseList.add(new JSONObject("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-10,\"message\":\"Unknown Host\"},\"id\":null}"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } catch (ProtocolException e) {
                    Log.e(Constants.TAG, "API: Protocol Exception.");
                    e.printStackTrace();
                    try {
                        responseList.add(new JSONObject("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-20,\"message\":\"Protocol Exception\"},\"id\":null}"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } catch (SocketTimeoutException e) {
                    Log.e(Constants.TAG, String.format("API: Connection timed out.\tRequest: %s", params[0].Command));
                    e.printStackTrace();
                    try {
                        responseList.add(new JSONObject("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-30,\"message\":\"Network Timeout\"},\"id\":null}"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e(Constants.TAG, String.format("API: CatchAll.\tRequest: %s", params[0].Command));
                    e.printStackTrace();
                    try {
                        responseList.add(new JSONObject("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-1,\"message\":\"" + e.getMessage() + "\"},\"id\":null}\""));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            return new KanboardResult(params[0], responseList.toArray(new JSONObject[]{}), httpResponseCode);
        }
        @Override
        protected void onPostExecute(KanboardResult s) {
            // Handle Errors
            if (s == null || s.Result == null || s.Result[0] == null) {
                KanboardError res = new KanboardError(null, null, 0);
                for (OnErrorListener l: onErrorListeners)
                    l.onError(res);
                return;
            }
            if (s.Result[0].has("error") || s.ReturnCode >= 400) {
                Log.e(Constants.TAG, s.Result[0].toString());
                JSONObject err = s.Result[0].optJSONObject("error");
                KanboardError res = new KanboardError(s.Request, err, s.ReturnCode);
                for (OnErrorListener l: onErrorListeners)
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
                for (OnGetMeListener l: onGetMeListeners)
                    l.onGetMe(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getVersion")) {
                String res;
                int[] version = new int[3];
                String tag = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = s.Result[0].getString("result");
                        if (res.startsWith("master")) {
                            Log.d(Constants.TAG, "Development Version");
                            version[0] = -1;
                            version[1] = -1;
                            version[2] = -1;
                            tag = res;
                        } else {
                            try {
                                Pattern regex = Pattern.compile("^[vV]{0,1}(\\d+)\\.(\\d+)\\.(\\d+)(?:,(.*)){0,1}$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                                Matcher regexMatcher = regex.matcher(res);
                                if (regexMatcher.find()) {
                                    version[0] = Integer.parseInt(regexMatcher.group(1));
                                    version[1] = Integer.parseInt(regexMatcher.group(2));
                                    version[2] = Integer.parseInt(regexMatcher.group(3));
                                    if (regexMatcher.groupCount() == 4 && regexMatcher.group(4) != null)
                                        tag = regexMatcher.group(4).trim();
                                }
                            } catch (PatternSyntaxException ex) {
                                ex.printStackTrace();
                                throw ex;
                                // Syntax error in the regular expression
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetVersionListener l: onGetVersionListeners)
                    l.onGetVersion(success, version, tag);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getDefaultTaskColor")) {
                String res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = s.Result[0].getString("result");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetDefaultColorListener l: onGetDefaultColorListeners) {
                    l.onGetDefaultColor(success, res);
                }
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getDefaultTaskColors")) {
                Dictionary<String, KanboardColor> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new Hashtable<>();
                        JSONObject jso = s.Result[0].getJSONObject("result");
                        Iterator<String> iter = jso.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            res.put(key, new KanboardColor(key, jso.getJSONObject(key)));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetDefaultColorsListener l: onGetDefaultColorsListeners) {
                    l.onGetDefaultColors(success, res);
                }
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
                    for (OnGetMyProjectsListListener l: onGetMyProjectsListListeners)
                        l.onGetMyProjectsList(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getMyProjects")) {
                List<KanboardProject> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new ArrayList<>();
                        JSONArray jsa = s.Result[0].getJSONArray("result");
                        for (int i = 0; i < jsa.length(); i++) {
                            res.add(new KanboardProject(jsa.getJSONObject(i)));
                        }
                    }
                } catch (JSONException | MalformedURLException e) {
                    e.printStackTrace();
                }
                    for (OnGetMyProjectsListener l: onGetMyProjectsListeners)
                        l.onGetMyProjects(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getMyDashboard")) {
                KanboardDashboard res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        Object dash = s.Result[0].get("result");
                        res = new KanboardDashboard(dash);
                    }
                } catch (JSONException | MalformedURLException e) {
                    e.printStackTrace();
                    for (OnErrorListener l: onErrorListeners) {
                        try {
                            l.onError(new KanboardError(s.Request, new JSONObject("{\"message\": \"\", \"code\": -50}"), 200));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                for (OnGetMyDashboardListener l: onGetMyDashboardListeners)
                    l.onGetMyDashboard(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getMyActivityStream")) {
                List<KanboardActivity> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new ArrayList<>();
                        JSONArray jsa = s.Result[0].getJSONArray("result");
                        for (int i = 0; i < jsa.length(); i++) {
                            res.add(new KanboardActivity(jsa.getJSONObject(i)));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetMyActivityStreamListener l: onGetMyActivityStreamListeners)
                    l.onGetMyActivityStream(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getMyOverdueTasks")) {
                List<KanboardTask> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new ArrayList<>();
                        JSONArray jsa = s.Result[0].getJSONArray("result");
                        for (int i = 0; i < jsa.length(); i++) {
                            res.add(new KanboardTask(jsa.getJSONObject(i)));
                        }
                    }
                } catch (JSONException | MalformedURLException e) {
                    e.printStackTrace();
                }
                for (OnGetMyOverdueTasksListener l: onGetMyOverdueTasksListeners)
                    l.onGetMyOverdueTasks(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getProjectById")) {
                KanboardProject res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new KanboardProject(s.Result[0].getJSONObject("result"));
                    }
                } catch (JSONException | MalformedURLException e) {
                    e.printStackTrace();
                }
                for (OnGetProjectByIdListener l: onGetProjectByIdListeners)
                    l.onGetProjectById(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getColumns")) {
                List<KanboardColumn> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new ArrayList<>();
                        JSONArray jsa = s.Result[0].getJSONArray("result");
                        for (int i = 0; i < jsa.length(); i++) {
                            res.add(new KanboardColumn(jsa.getJSONObject(i)));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetColumnsListener l: onGetColumnsListeners)
                    l.onGetColumns(success, res);
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

            if (s.Request.Command.equalsIgnoreCase("getAllTasks")) {
                List<KanboardTask> res = null;
                int status = 0;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        status = (new JSONObject(s.Request.JSON[0])).getJSONObject("params").getInt("status_id");
                        res = new ArrayList<>();
                        JSONArray jsa = s.Result[0].getJSONArray("result");
                        for (int i = 0; i < jsa.length(); i++) {
                            res.add(new KanboardTask(jsa.getJSONObject(i)));
                        }
                    }
                } catch (JSONException | MalformedURLException e) {
                    e.printStackTrace();
                }
                for (OnGetAllTasksListener l: onGetAllTasksListener)
                    l.onGetAllTasks(success, status, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getOverdueTasksByProject")) {
                List<KanboardTask> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new ArrayList<>();
                        JSONArray jsa = s.Result[0].getJSONArray("result");
                        for (int i = 0; i < jsa.length(); i++) {
                            res.add(new KanboardTask(jsa.getJSONObject(i)));
                        }
                    }
                } catch (JSONException | MalformedURLException e) {
                    e.printStackTrace();
                }
                for (OnGetOverdueTasksByProjectListener l: onGetOverdueTasksByProjectListeners)
                    l.onGetOverdueTasksByProject(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("createTask")) {
                Integer res = null;
                try {
                    if (!s.Result[0].getString("result").equalsIgnoreCase("false")) {
                        success = true;
                        res = s.Result[0].getInt("result");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnCreateTaskListener l: onCreateTaskListeners)
                    l.onCreateTask(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("updateTask")) {
                success = s.Result[0].optBoolean("result", false);
                for (OnUpdateTaskListener l: onUpdateTaskListeners)
                    l.onUpdateTask(success);
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
                success = s.Result[0].optBoolean("result", false);
                for (OnOpenTaskListener l: onOpenTaskListeners)
                    l.onOpenTask(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("closeTask")) {
                success = s.Result[0].optBoolean("result", false);
                for (OnCloseTaskListener l: onCloseTaskListeners)
                    l.onCloseTask(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("removeTask")) {
                success = s.Result[0].optBoolean("result", false);
                for (OnRemoveTaskListener l: onRemoveTaskListeners)
                    l.onRemoveTask(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("moveTaskPosition")) {
                success = s.Result[0].optBoolean("result", false);
                for (OnMoveTaskPositionListener l: onMoveTaskPositionListeners)
                    l.onMoveTaskPosition(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("moveTaskToProject")) {
                success = s.Result[0].optBoolean("result", false);
                for (OnMoveTaskToProjectListener l: onMoveTaskToProjectListeners)
                    l.onMoveTaskToProject(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("duplicateTaskToProject")) {
                Integer res = null;
                if (s.Result[0].has("result")) {
                    success = true;
                    res = s.Result[0].optInt("result");
                }
                for (OnDuplicateTaskToProjectListener l: onDuplicateTaskToProjectListeners)
                    l.onDuplicateTaskToProject(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getAllTaskFiles")) {
                List<KanboardTaskFile> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new ArrayList<>();
                        JSONArray jsa = s.Result[0].getJSONArray("result");
                        for (int i = 0; i < jsa.length(); i++) {
                            res.add(new KanboardTaskFile(jsa.getJSONObject(i)));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetAllTaskFilesListener l: onGetAllTaskFilesListeners)
                    l.onGetAllTaskFiles(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("removeTaskFile")) {
                success = s.Result[0].optBoolean("result", false);
                for (OnRemoveTaskFileListener l: onRemoveTaskFileListeners)
                    l.onRemoveTaskFile(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("downloadTaskFile")) {
                int id = s.Result[0].optInt("id");
                String data = s.Result[0].optString("result", "");
                success = !data.isEmpty();
                for (OnDownloadTaskFileListener l: onDownloadTaskFileListeners)
                    l.onDownloadTaskFile(success, id, data);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getActiveSwimlanes")) {
                List<KanboardSwimlane> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new ArrayList<>();
                        JSONArray jsa = s.Result[0].getJSONArray("result");
                        for (int i = 0; i < jsa.length(); i++) {
                            res.add(new KanboardSwimlane(jsa.getJSONObject(i)));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetActiveSwimlanesListener l: onGetActiveSwimlanesListeners)
                    l.onGetActiveSwimlanes(success, res);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getDefaultSwimlane")) {
                KanboardSwimlane res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        JSONObject jso = s.Result[0].getJSONObject("result");
                        res = new KanboardSwimlane(jso);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetSwimlaneListener l: onGetDefaultSwimlaneListeners)
                    l.onGetSwimlane(success, res);
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

            if (s.Request.Command.equalsIgnoreCase("getAllCategories")) {
                List<KanboardCategory> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new ArrayList<>();
                        JSONArray jsa = s.Result[0].getJSONArray("result");
                        for (int i = 0; i < jsa.length(); i++) {
                            res.add(new KanboardCategory(jsa.getJSONObject(i)));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (OnGetAllCategoriesListener l: onGetAllCategoriesListeners)
                    l.onGetAllCategories(success, res);
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
                success = s.Result[0].optBoolean("result", false);
                for (OnUpdateCommentListener l: onUpdateCommentListeners)
                    l.onUpdateComment(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("removeComment")) {
                success = s.Result[0].optBoolean("result", false);
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
                success = s.Result[0].optBoolean("result", false);
                for (OnUpdateSubtaskListener l: onUpdateSubtaskListeners)
                    l.onUpdateSubtask(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("removeSubtask")) {
                success = s.Result[0].optBoolean("result", false);
                for (OnRemoveSubtaskListener l: onRemoveSubtaskListeners)
                    l.onRemoveSubtask(success);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("hasSubtaskTimer")) {
                try {
                    hasSubtaskTimerSet.remove(new JSONObject(s.Request.JSON[0]).getJSONArray("params").getInt(0));
                    success = s.Result[0].optBoolean("result", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (s.Request.Listener != null)
                    ((OnSubtaskTimetrackingListener) s.Request.Listener).onSubtaskTimetracking(success, 0);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("setSubtaskStartTime")) {
                success = s.Result[0].optBoolean("result", false);
                if (s.Request.Listener != null)
                    ((OnSubtaskTimetrackingListener) s.Request.Listener).onSubtaskTimetracking(success, 0);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("setSubtaskEndTime")) {
                success = s.Result[0].optBoolean("result", false);
                if (s.Request.Listener != null)
                    ((OnSubtaskTimetrackingListener) s.Request.Listener).onSubtaskTimetracking(success, 0);
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getSubtaskTimeSpent")) {
                double time;
                try {
                    getSubtaskTimeSpentSet.remove(new JSONObject(s.Request.JSON[0]).getJSONArray("params").getInt(0));
                    time = s.Result[0].getDouble("result");
                    success = true;
                } catch (JSONException e) {
                    time = 0;
                    success = false;
                }
                if (s.Request.Listener != null)
                    ((OnSubtaskTimetrackingListener) s.Request.Listener).onSubtaskTimetracking(success, time);
                return;
            }

//            if (s.Request.Command.equalsIgnoreCase("KD_getDashboard")) {
//                KanboardDashboard res = null;
//                try {
//                    if (s.Result[0].has("result") && s.Result[1].has("result") && s.Result[2].has("result")) {
//                        success = true;
//                        res = new KanboardDashboard(s.Result[0].getJSONObject("result"), s.Result[1].getJSONArray("result"), s.Result[2].getJSONArray("result"));
//                    }
//                } catch (JSONException | MalformedURLException e) {
//                    e.printStackTrace();
//                }
//                return;
//            }

//            if (s.Request.Command.equalsIgnoreCase("KD_getProjectById")) {
//                KanboardProject res = null;
//                try {
//                    if (s.Result[0].has("result") && s.Result[1].has("result") && s.Result[2].has("result") && s.Result[3].has("result") && s.Result[4].has("result") && s.Result[5].has("result") && s.Result[6].has("result")) {
//                        success = true;
//                        res = new KanboardProject(s.Result[0].optJSONObject("result"),
//                                                  s.Result[1].optJSONArray("result"),
//                                                  s.Result[2].optJSONArray("result"),
//                                                  s.Result[3].optJSONArray("result"),
//                                                  s.Result[4].optJSONArray("result"),
//                                                  s.Result[5].optJSONArray("result"),
//                                                  s.Result[6].optJSONArray("result"),
//                                                  s.Result[7].optJSONObject("result"));
//                    }
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//                return;
//            }
        }
    }

    private URL kanboardURL;
    private ThreadPoolExecutor threadPoolExecutor;
    private HashSet<OnGetMyDashboardListener> onGetMyDashboardListeners = new HashSet<>();
    private HashSet<OnGetMyActivityStreamListener> onGetMyActivityStreamListeners = new HashSet<>();
    private HashSet<OnGetMyOverdueTasksListener> onGetMyOverdueTasksListeners = new HashSet<>();
    private HashSet<OnGetProjectByIdListener> onGetProjectByIdListeners = new HashSet<>();
    private HashSet<OnGetColumnsListener> onGetColumnsListeners = new HashSet<>();
    private HashSet<OnGetAllCommentsListener> onGetAllCommentsListeners = new HashSet<>();
    private HashSet<OnGetAllTasksListener> onGetAllTasksListener = new HashSet<>();
    private HashSet<OnGetOverdueTasksByProjectListener> onGetOverdueTasksByProjectListeners = new HashSet<>();
    private HashSet<OnGetTaskListener> onGetTaskListeners = new HashSet<>();
    private HashSet<OnGetAllCategoriesListener> onGetAllCategoriesListeners = new HashSet<>();
    private HashSet<OnGetCategoryListener> onGetCategoryListeners = new HashSet<>();
    private HashSet<OnGetProjectUsersListener> onGetProjectUsersListeners = new HashSet<>();
    private HashSet<OnGetSwimlaneListener> onGetSwimlaneListeners = new HashSet<>();
    private HashSet<OnGetActiveSwimlanesListener> onGetActiveSwimlanesListeners = new HashSet<>();
    private HashSet<OnGetSwimlaneListener> onGetDefaultSwimlaneListeners = new HashSet<>();
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
    private HashSet<OnCreateTaskListener> onCreateTaskListeners = new HashSet<>();
    private HashSet<OnUpdateTaskListener> onUpdateTaskListeners = new HashSet<>();
    private HashSet<OnMoveTaskPositionListener> onMoveTaskPositionListeners = new HashSet<>();
    private HashSet<OnMoveTaskToProjectListener> onMoveTaskToProjectListeners = new HashSet<>();
    private HashSet<OnDuplicateTaskToProjectListener> onDuplicateTaskToProjectListeners = new HashSet<>();
    private HashSet<OnGetVersionListener> onGetVersionListeners = new HashSet<>();
    private HashSet<OnErrorListener> onErrorListeners = new HashSet<>();
    private HashSet<OnGetDefaultColorListener> onGetDefaultColorListeners = new HashSet<>();
    private HashSet<OnGetDefaultColorsListener> onGetDefaultColorsListeners = new HashSet<>();
    private HashSet<OnGetMyProjectsListener> onGetMyProjectsListeners = new HashSet<>();
    private HashSet<OnGetMyProjectsListListener> onGetMyProjectsListListeners = new HashSet<>();
    private HashSet<OnGetAllTaskFilesListener> onGetAllTaskFilesListeners = new HashSet<>();
    private HashSet<OnRemoveTaskFileListener> onRemoveTaskFileListeners = new HashSet<>();
    private HashSet<OnDownloadTaskFileListener> onDownloadTaskFileListeners = new HashSet<>();

    //HashSets for AsyncTask limiting
    private HashSet<Integer> hasSubtaskTimerSet = new HashSet<>();
    private HashSet<Integer> getSubtaskTimeSpentSet = new HashSet<>();

    public KanboardAPI(String serverURL, final String username, final String password) throws IOException {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }

        });
        kanboardURL = KanboardAPI.sanitizeURL(serverURL.trim());
        Log.i(Constants.TAG, String.format("Host uses %s", kanboardURL.getProtocol()));
//        threadPoolExecutor = new ThreadPoolExecutor(12, 12, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(256));
        threadPoolExecutor = (ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;
        threadPoolExecutor.setCorePoolSize(12);
        threadPoolExecutor.setMaximumPoolSize(12);
    }

    // Event Listeners

    public void addOnGetMyDashboardListener(@NonNull OnGetMyDashboardListener listener) {
        onGetMyDashboardListeners.add(listener);
    }

    public void removeOnGetMyDashboardListener(@NonNull OnGetMyDashboardListener listener) {
        onGetMyDashboardListeners.remove(listener);
    }

    public void addOnGetMyActivityStreamListener(@NonNull OnGetMyActivityStreamListener listener) {
        onGetMyActivityStreamListeners.add(listener);
    }

    public void removeOnGetMyActivityStreamListener(@NonNull OnGetMyActivityStreamListener listener) {
        onGetMyActivityStreamListeners.remove(listener);
    }

    public void addOnGetMyOverdueTasksListener(@NonNull OnGetMyOverdueTasksListener listener) {
        onGetMyOverdueTasksListeners.add(listener);
    }

    public void removeOnGetMyOverdueTasksListener(@NonNull OnGetMyOverdueTasksListener listener) {
        onGetMyOverdueTasksListeners.remove(listener);
    }

    public void addOnGetProjectByIdListener(@NonNull OnGetProjectByIdListener listener) {
        onGetProjectByIdListeners.add(listener);
    }

    public void removeOnGetProjectByIdListener(@NonNull OnGetProjectByIdListener listener) {
        onGetProjectByIdListeners.remove(listener);
    }

    public void addOnGetColumnsListener(@NonNull OnGetColumnsListener listener) {
        onGetColumnsListeners.add(listener);
    }

    public void removeOnGetColumnsListener(@NonNull OnGetColumnsListener listener) {
        onGetColumnsListeners.remove(listener);
    }

    public void addOnGetAllCommentsListener(@NonNull OnGetAllCommentsListener listener) {
        onGetAllCommentsListeners.add(listener);
    }

    public void removeOnGetAllCommentsListener(@NonNull OnGetAllCommentsListener listener) {
        onGetAllCommentsListeners.remove(listener);
    }

    public void addOnGetActiveSwimlanesListener(@NonNull OnGetActiveSwimlanesListener listener) {
        onGetActiveSwimlanesListeners.add(listener);
    }

    public void removeOnGetActiveSwimlanesListener(@NonNull OnGetActiveSwimlanesListener listener) {
        onGetActiveSwimlanesListeners.remove(listener);
    }

    public void addOnGetSwimlaneListener(@NonNull OnGetSwimlaneListener listener) {
        onGetSwimlaneListeners.add(listener);
    }

    public void removeOnGetSwimlaneListener(@NonNull OnGetSwimlaneListener listener) {
        onGetSwimlaneListeners.remove(listener);
    }

    public void addOnGetDefaultSwimlaneListener(@NonNull OnGetSwimlaneListener listener) {
        onGetDefaultSwimlaneListeners.add(listener);
    }

    public void removeOnGetDefaultSwimlaneListener(@NonNull OnGetSwimlaneListener listener) {
        onGetDefaultSwimlaneListeners.remove(listener);
    }

    public void addOnGetAllTasksListener(@NonNull OnGetAllTasksListener listener) {
        onGetAllTasksListener.add(listener);
    }

    public void removeGetAllTasksListener(@NonNull OnGetAllTasksListener listener) {
        onGetAllTasksListener.remove(listener);
    }

    public void addOnGetOverdueTasksByProjectListener(@NonNull OnGetOverdueTasksByProjectListener listener) {
        onGetOverdueTasksByProjectListeners.add(listener);
    }

    public void removeGetOverdueTasksByProjectListener(@NonNull OnGetOverdueTasksByProjectListener listener) {
        onGetOverdueTasksByProjectListeners.remove(listener);
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

    public void addOnMoveTaskPositionListener(@NonNull OnMoveTaskPositionListener listener) {
        onMoveTaskPositionListeners.add(listener);
    }

    public void removeOnMoveTaskPositionListener(@NonNull OnMoveTaskPositionListener listener) {
        onMoveTaskPositionListeners.remove(listener);
    }

    public void addOnMoveTaskToProjectListener(@NonNull OnMoveTaskToProjectListener listener) {
        onMoveTaskToProjectListeners.add(listener);
    }

    public void removeOnMoveTaskToProjectListener(@NonNull OnMoveTaskToProjectListener listener) {
        onMoveTaskToProjectListeners.remove(listener);
    }

    public void addOnDuplicateTaskToProjectListener(@NonNull OnDuplicateTaskToProjectListener listener) {
        onDuplicateTaskToProjectListeners.add(listener);
    }

    public void removeOnDuplicateTaskToProjectListener(@NonNull OnDuplicateTaskToProjectListener listener) {
        onDuplicateTaskToProjectListeners.remove(listener);
    }

    public void addOnGetAllCategoriesListener(@NonNull OnGetAllCategoriesListener listener) {
        onGetAllCategoriesListeners.add(listener);
    }

    public void removeOnGetAllCategoriesListener(@NonNull OnGetAllCategoriesListener listener) {
        onGetAllCategoriesListeners.remove(listener);
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

    public void addOnCreateTaskListener(@NonNull OnCreateTaskListener listener) {
        onCreateTaskListeners.add(listener);
    }

    public void removeOnCreateTaskListener(@NonNull OnCreateTaskListener listener) {
        onCreateTaskListeners.remove(listener);
    }

    public void addOnUpdateTaskListener(@NonNull OnUpdateTaskListener listener) {
        onUpdateTaskListeners.add(listener);
    }

    public void removeOnUpdateTaskListener(@NonNull OnUpdateTaskListener listener) {
        onUpdateTaskListeners.remove(listener);
    }

    public void addOnGetVersionListener(@NonNull OnGetVersionListener listener) {
        onGetVersionListeners.add(listener);
    }

    public void removeOnGetVersionListener(@NonNull OnGetVersionListener listener) {
        onGetVersionListeners.remove(listener);
    }

    public void addOnGetDefaultColorListener(@NonNull OnGetDefaultColorListener listener) {
        onGetDefaultColorListeners.add(listener);
    }

    public void removeOnGetDefaultColorListener(@NonNull OnGetDefaultColorListener listener) {
        onGetDefaultColorListeners.remove(listener);
    }

    public void addOnGetDefaultColorsListener(@NonNull OnGetDefaultColorsListener listener) {
        onGetDefaultColorsListeners.add(listener);
    }

    public void removeOnGetDefaultColorsListener(@NonNull OnGetDefaultColorsListener listener) {
        onGetDefaultColorsListeners.remove(listener);
    }

    public void addOnGetMyProjectsListener(@NonNull OnGetMyProjectsListener listener) {
        onGetMyProjectsListeners.add(listener);
    }

    public void removeOnGetMyProjectsListener(@NonNull OnGetMyProjectsListener listener) {
        onGetMyProjectsListeners.remove(listener);
    }

    public void addErrorListener(@NonNull OnErrorListener listener) {
        onErrorListeners.add(listener);
    }

    public void removeErrorListener(@NonNull OnErrorListener listener) {
        onErrorListeners.remove(listener);
    }

    public void addOnGetMyProjectsListListeners(@NonNull OnGetMyProjectsListListener listener) {
        onGetMyProjectsListListeners.add(listener);
    }

    public void removeOnGetMyProjectsListListeners(@NonNull OnGetMyProjectsListListener listener) {
        onGetMyProjectsListListeners.remove(listener);
    }

    public void addOnGetAllTaskFilesListListeners(@NonNull OnGetAllTaskFilesListener listener) {
        onGetAllTaskFilesListeners.add(listener);
    }

    public void removeOnGetAllTaskFilesListeners(@NonNull OnGetAllTaskFilesListener listener) {
        onGetAllTaskFilesListeners.remove(listener);
    }

    public void addOnRemoveTaskFileListeners(@NonNull OnRemoveTaskFileListener listener) {
        onRemoveTaskFileListeners.add(listener);
    }

    public void removeRemoveTaskFileListeners(@NonNull OnRemoveTaskFileListener listener) {
        onRemoveTaskFileListeners.remove(listener);
    }

    public void addOnDownloadTaskFileListeners(@NonNull OnDownloadTaskFileListener listener) {
        onDownloadTaskFileListeners.add(listener);
    }

    public void removeDownloadTaskFileListeners(@NonNull OnDownloadTaskFileListener listener) {
        onDownloadTaskFileListeners.remove(listener);
    }

    // API Calls

    public void getMe() {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getMe());
    }

    public void getVersion() {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getVersion());
    }

    public void getDefaultTaskColors() {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getDefaultTaskColors());
    }

    public void getDefaultTaskColor() {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getDefaultTaskColor());
    }

    public void getMyProjectsList() {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getMyProjectsList());
    }

    public void getMyProjects() {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getMyProjects());
    }

    public void getMyDashboard() {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getMyDashboard());
    }

    public void getMyActivityStream() {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getMyActivityStream());
    }

    public void getMyOverdueTasks() {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getMyOverdueTasks());
    }

    public void getProjectById(int projectid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getProjectById(projectid));
    }

    public void getProjectUsers(int projectid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getProjectUsers(projectid));
    }

    public void getColumns(int projectid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getColumns(projectid));
    }

    public void getAllTasks(int projectid, int status) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getAllTasks(projectid, status));
    }

    public void getOverdueTasksByProject(int projectid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getOverdueTasksByProject(projectid));
    }

    public void createTask(@NonNull String title, int projectid, @Nullable String colorid,
                           @Nullable Integer columnid, @Nullable Integer ownerid,
                           @Nullable Integer creatorid, @Nullable Date duedate,
                           @Nullable String description, @Nullable Integer categoryid,
                           @Nullable Integer score, @Nullable Integer swimlaneid,
                           @Nullable Integer priority, @Nullable Integer recurrencestatus,
                           @Nullable Integer recurrencetrigger, @Nullable Integer recurrencefactor,
                           @Nullable Integer recurrencetimeframe, @Nullable Integer recurrencebasedate,
                           @Nullable String[] tags, @Nullable Date starteddate) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.createTask(title,
                projectid, colorid, columnid, ownerid, creatorid, duedate, description, categoryid,
                score, swimlaneid, priority, recurrencestatus, recurrencetrigger, recurrencefactor,
                recurrencetimeframe, recurrencebasedate, tags, starteddate));
    }

    public void updateTask(int taskid, @NonNull String title, @Nullable String colorid,
                           @Nullable Integer ownerid,
                           @Nullable Date duedate,
                           @Nullable String description, @Nullable Integer categoryid,
                           @Nullable Integer score,
                           @Nullable Integer priority, @Nullable Integer recurrencestatus,
                           @Nullable Integer recurrencetrigger, @Nullable Integer recurrencefactor,
                           @Nullable Integer recurrencetimeframe, @Nullable Integer recurrencebasedate,
                           @Nullable String[] tags, @Nullable Date starteddate) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.updateTask(taskid,
                title, colorid, ownerid, duedate, description, categoryid, score,
                priority, recurrencestatus, recurrencetrigger, recurrencefactor,
                recurrencetimeframe, recurrencebasedate, tags, starteddate));
    }

    public void getTask(int taskid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getTask(taskid));
    }

    public void openTask(int taskid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.openTask(taskid));
    }

    public void closeTask(int taskid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.closeTask(taskid));
    }

    public void removeTask(int taskid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.removeTask(taskid));
    }

    public void moveTaskPosition(int projectid, int taskid, int columnid, int position,
                                 int swimlaneid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor,
                KanboardRequest.moveTaskPosition(projectid, taskid, columnid, position, swimlaneid));
    }

    public void moveTaskToProject(int projectid, int taskid, @Nullable Integer columnid,
                                  @Nullable Integer position, @Nullable Integer swimlaneid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor,
                KanboardRequest.moveTaskToProject(projectid, taskid, columnid, position, swimlaneid));
    }

    public void duplicateTaskToProject(int projectid, int taskid, @Nullable Integer columnid,
                                  @Nullable Integer position, @Nullable Integer swimlaneid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor,
                KanboardRequest.duplicateTaskToProject(projectid, taskid, columnid, position, swimlaneid));
    }

    public void getAllComments(int taskid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getAllComments(taskid));
    }

    public void createComment(int taskid, int userid, String comment) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.createComment(taskid, userid, comment));
    }

    public void updateComment(int commentid, @NonNull String comment) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.updateComment(commentid, comment));
    }

    public void removeComment(int commentid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.removeComment(commentid));
    }

    public void getActiveSwimlanes(int projectid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getActiveSwimlanes(projectid));
    }

    public void getDefaultSwimlane(int projectid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getDefaultSwimlane(projectid));
    }

    public void getSwimlane(int swimlaneid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getSwimlane(swimlaneid));
    }

    public void getAllCategories(int projectid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getAllCategories(projectid));
    }

    public void getCategory(int categoryid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getCategrory(categoryid));
    }

    public void getAllSubtasks(int taskid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getAllSubtasks(taskid));
    }

    public void createSubtask(int taskid, @NonNull String title, @Nullable Integer userid,
                              @Nullable Integer timeestimated, @Nullable Integer timespent, @Nullable Integer status) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.createSubtask(taskid, title, userid, timeestimated, timespent, status));
    }

    public void updateSubtask(int subtaskid, int taskid, @Nullable String title, @Nullable Integer userid,
                              @Nullable Integer timeestimated, @Nullable Integer timespent, @Nullable Integer status) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.updateSubtask(subtaskid, taskid, title, userid, timeestimated, timespent, status));
    }

    public void removeSubtask(int subtaskid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.removeSubtask(subtaskid));
    }

    public void hasSubtaskTimer(int subtaskid, int userid, @NonNull OnSubtaskTimetrackingListener listener) {
        if (!hasSubtaskTimerSet.contains(subtaskid)) {
            hasSubtaskTimerSet.add(subtaskid);
            new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.hasSubtaskTimer(subtaskid, userid, listener));
        }
    }

    public void setSubtaskStartTime(int subtaskid, int userid, @NonNull OnSubtaskTimetrackingListener listener) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.setSubtaskStartTime(subtaskid, userid, listener));
    }

    public void setSubtaskEndTime(int subtaskid, int userid, @NonNull OnSubtaskTimetrackingListener listener) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.setSubtaskEndTime(subtaskid, userid, listener));
    }

    public void getSubtaskTimeSpent(int subtaskid, int userid, @NonNull OnSubtaskTimetrackingListener listener) {
        if (!getSubtaskTimeSpentSet.contains(subtaskid)) {
            getSubtaskTimeSpentSet.add(subtaskid);
            new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getSubtaskTimeSpent(subtaskid, userid, listener));
        }
    }

    public void getAllTaskFiles(int taskid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.getAllTaskFiles(taskid));
    }

    public void removeTaskFile(int fileid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.removeTaskFile(fileid));
    }

    public void downloadTaskFile(int fileid) {
        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.downloadTaskFile(fileid));
    }

//    public void KB_getDashboard() {
//        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.KD_getDashboard());
//    }
//
//    public void KB_getProjectById(int projectid) {
//        new KanboardAsync().executeOnExecutor(threadPoolExecutor, KanboardRequest.KD_getProjectById(projectid));
//    }

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
