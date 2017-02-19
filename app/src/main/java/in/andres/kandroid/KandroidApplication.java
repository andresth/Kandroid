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


import android.app.Application;
import android.util.Log;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.HttpSender;

@ReportsCrashes(//mailTo = "kandroid@andres.in",
                formUri = "https://uber.andres.in/crashreport/report.php",
                //customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.USER_COMMENT, ReportField.STACK_TRACE, ReportField.LOGCAT },
                mode = ReportingInteractionMode.DIALOG,
                excludeMatchingSharedPreferencesKeys={"username", "password", "serverurl"},
                httpMethod = HttpSender.Method.PUT,
                reportType = HttpSender.Type.JSON,
                logcatArguments = { "-t", "200", "-v", "time", "Kandroid:d", "InstantRun:s", "*:e" },
                resDialogIcon = android.R.drawable.stat_notify_error,
                resDialogTitle = R.string.acra_dialog_title,
//                resDialogText = R.string.acra_dialog_text,
                resDialogText = R.string.acra_dialog_text_upload,
//                resDialogPositiveButtonText = R.string.acra_send_mail,
                resDialogCommentPrompt = R.string.acra_dialog_comment_prompt)
public class KandroidApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constants.TAG, "Start ACRA");
        ACRA.init(this);
    }
}
