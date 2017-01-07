package in.andres.kandroid.kanboard;


import android.os.AsyncTask;
import android.util.Log;

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
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class KanboardAPI {

    class KanboardAsync extends AsyncTask<KanboardRequest, Void, KanboardResult> {
        @Override
        protected KanboardResult doInBackground(KanboardRequest... params) {
            HttpsURLConnection con = null;
            try {
                List<JSONObject> responseList = new ArrayList<>();
                for (String s: params[0].JSON) {
                    con = (HttpsURLConnection) kanboardURL.openConnection();
                    con.setRequestMethod("POST");
                    con.setConnectTimeout(120000);
                    con.setReadTimeout(120000);
                    con.setDoOutput(true);
                    con.setDoInput(true);
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

                    JSONObject response = null;
                    try {
                        response = new JSONObject(responseStr.toString());
                    } catch (JSONException e) {
                        response = null;
                    }
                    responseList.add(response);
                }

                return new KanboardResult(params[0], responseList.toArray(new JSONObject[] {}), con.getResponseCode());
            } catch (SocketTimeoutException e) {
                try {
                    return new KanboardResult(params[0], new JSONObject[] {new JSONObject("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":0,\"message\":\"Network Timeout\"},\"id\":null}")}, 0);
                } catch (JSONException e1) {}
            } catch (Exception e) {
                try {
                    return new KanboardResult(params[0], new JSONObject[] {new JSONObject("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-1,\"message\":\"" + e.getMessage() + "\"},\"id\":null}")}, 0);
                } catch (JSONException e1) {}
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
            }
            if (s.Result[0].has("error") || s.ReturnCode >= 400) {
                JSONObject err = s.Result[0].optJSONObject("error");
                KanboardError res = new KanboardError(s.Request, err, s.ReturnCode);
                for (KanbordEvents l: listeners)
                    l.onError(res);
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
                return;
            }

            if (s.Request.Command.equalsIgnoreCase("getMyProjectsList")) {
                List<KanboardProjectInfo> res = null;
                try {
                    if (s.Result[0].has("result")) {
                        success = true;
                        res = new ArrayList<KanboardProjectInfo>();
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
        }
    }

    private URL kanboardURL;
    private HashSet<KanbordEvents> listeners = new HashSet<>();

    public KanboardAPI(String serverURL, final String username, final String password) throws MalformedURLException, IOException {
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

    public void addListener(KanbordEvents listener) {
        listeners.add(listener);
    }

    public void removeListener(KanbordEvents listener) {
        listeners.remove(listener);
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

    public void KB_getDashboard() {
        new KanboardAsync().execute(KanboardRequest.KD_getDashboard());
    }

    // TODO: add API calls

    public static boolean StringToBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("1");
    }
}
