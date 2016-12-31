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
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;


public class KanboardAPI extends AsyncTask<KanboardTask, Void, KanboardResult> {
    private URL kanboardURL;
    private HashSet<KanbordEvents> listeners = new HashSet<>();
    private HttpURLConnection kanboard;

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
        this.execute(KanboardTask.getMe());
    }

    @Override
    protected KanboardResult doInBackground(KanboardTask... params) {
        HttpsURLConnection con = null;
        try {
            String data = "{\"jsonrpc\": \"2.0\", \"method\": \"getMe\", \"id\": 1}";
            con = (HttpsURLConnection) kanboardURL.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(params[0].json);
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            return new KanboardResult(params[0].command, response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(KanboardResult s) {
        boolean succsess = false;
        if (s.command == "getMe") {
            KanboardUserInfo res = null;
            try {
                JSONObject jo = new JSONObject(s.json);
                if (jo.has("result")) {
                    succsess = true;
                    JSONObject jso = jo.getJSONObject("result");
                    res = new KanboardUserInfo(jso);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (KanbordEvents l: this.listeners) {
                l.onGetMe(succsess, res);
            }
        }
//        for (RealUsernameHandler h: this.hRealUsername) {
//            h.returnRealUsername(true, s);
//        }
    }

}
