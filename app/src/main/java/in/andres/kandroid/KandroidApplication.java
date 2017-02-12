package in.andres.kandroid;


import android.app.Application;
import android.util.Log;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.HttpSender;

/**
 * Created by thomas on 11.02.17.
 */

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
