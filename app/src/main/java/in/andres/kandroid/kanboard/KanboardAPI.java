package in.andres.kandroid.kanboard;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KanboardAPI {

    class KanboardAsync extends AsyncTask<KanboardTask, Void, KanboardResult> {
        @Override
        protected KanboardResult doInBackground(KanboardTask... params) {
            HttpsURLConnection con = null;
            try {
                con = (HttpsURLConnection) kanboardURL.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setDoInput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(params[0].JSON);
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

                return new KanboardResult(params[0].Command, response, con.getResponseCode());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(KanboardResult s) {
            boolean success = false;
            if (s.Command =="getMe") {
                KanboardUserInfo res = null;
                try {
                    if (s.JSON.has("result") && (s.ReturnCode < 400)) {
                        success = true;
                        JSONObject jso = s.JSON.getJSONObject("result");
                        res = new KanboardUserInfo(jso);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (KanbordEvents l: listeners)
                    l.onGetMe(success, res);
                return;
            }

            if (s.Command =="getMyProjectsList") {
                List<KanboardProjectInfo> res = null;
                try {
                    if (s.JSON.has("result")) {
                        success = true;
                        res = new ArrayList<KanboardProjectInfo>();
                        JSONObject jso = s.JSON.getJSONObject("result");
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
        kanboardURL = new URL(serverURL);
    }

    public void addListener(KanbordEvents listener) {
        listeners.add(listener);
    }

    public void removeListener(KanbordEvents listener) {
        listeners.remove(listener);
    }

    public void getMe() throws IOException {
        new KanboardAsync().execute(KanboardTask.getMe());
    }

    public void getMyProjectsList() {
        new KanboardAsync().execute(KanboardTask.getMyProjectsList());
    }
}
