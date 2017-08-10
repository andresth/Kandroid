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

package in.andres.kandroid;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.cert.CertificateException;

import in.andres.kandroid.kanboard.KanboardAPI;


public class DownloadIntentService extends IntentService {
    public DownloadIntentService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(Constants.TAG, "Starting Download in Service");
        HttpURLConnection con = null;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(preferences.getString("username", ""), preferences.getString("password", "").toCharArray());
            }

        });

        String serverURL = preferences.getString("serverurl", "");

        try {
            URL kanboardURL = KanboardAPI.sanitizeURL(serverURL.trim());

            String request = intent.getStringExtra("request");
            String filename = intent.getStringExtra("filename");
            if (BuildConfig.DEBUG) {
                Log.d(Constants.TAG, request);
                Log.d(Constants.TAG, filename);
            }

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setContentTitle(getString(R.string.service_downloading))
                    .setContentText(filename)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setProgress(100, 0, true);

            mNotificationManager.notify(554, notificationBuilder.build());

            con = KanboardAPI.openConnection(kanboardURL, request);

            if (con == null) {
                Log.e(Constants.TAG, "DownloadService: Unable to connect to host");
                notificationBuilder.setContentTitle(getText(R.string.error_host_unknown))
                        .setOngoing(false)
                        .setSmallIcon(android.R.drawable.stat_notify_error)
                        .setProgress(0, 0, false);
                mNotificationManager.notify(554, notificationBuilder.build());
                return;
            }

            if (BuildConfig.DEBUG) {
                Log.d(Constants.TAG, Integer.toString(con.getResponseCode()));
            }

            int resultSize = con.getContentLength();

            StringBuilder contentString = new StringBuilder();

            InputStreamReader in;
            if (con.getResponseCode() < 400)
                in = (new InputStreamReader(con.getInputStream()));
            else
                in = (new InputStreamReader(con.getErrorStream()));

            char[] data = new char[1024];
            int count;
            int bytesRead = 0;

            while ((count = in.read(data)) != -1) {
                bytesRead += count;
                contentString.append(data);
                if (BuildConfig.DEBUG) {
                    Log.d(Constants.TAG, Integer.toString(bytesRead));
                }
            }

            JSONObject jsonData = new JSONObject(contentString.toString());
            String encodedData = jsonData.optString("result", "");
            if (encodedData.isEmpty()) {
                Log.d(Constants.TAG, "Ist leer");
                notificationBuilder.setContentTitle(getText(R.string.service_result_empty))
                        .setOngoing(false)
                        .setSmallIcon(android.R.drawable.stat_notify_error)
                        .setProgress(0, 0, false);
                mNotificationManager.notify(554, notificationBuilder.build());
                return;
            }
            byte[] decodedData = Base64.decode(encodedData, Base64.DEFAULT);

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            FileOutputStream fs = new FileOutputStream(file);
            fs.write(decodedData);
            fs.close();

            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString()));
            if (mime == null) {
                mime = "application/octet-stream";
            }
            if (BuildConfig.DEBUG) {
                Log.d(Constants.TAG, Uri.fromFile(file).toString());
                Log.d(Constants.TAG, mime);
            }

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            dm.addCompletedDownload(file.getName(), getString(R.string.download_manager_name), false, mime, file.getPath(), file.length(), true);
            Thread.sleep(500);
            mNotificationManager.cancel(554);
        } catch (MalformedURLException e) {
            // Do Something
        } catch (IOException e) {
            Log.d(Constants.TAG, e.toString());
            // Do Something
        } catch (CertificateException e) {
            Log.d(Constants.TAG, e.toString());
            // Do Something
        } catch (Exception e) {
            Log.d(Constants.TAG, e.toString());
            // Do Something
        }

    }
}
