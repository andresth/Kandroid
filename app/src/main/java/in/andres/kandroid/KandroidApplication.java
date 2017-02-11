package in.andres.kandroid;


import android.app.Application;
import android.util.Log;

import org.acra.*;
import org.acra.annotation.*;

/**
 * Created by thomas on 11.02.17.
 */

@ReportsCrashes(mailTo = "kandroid@andres.in",
                customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },
                mode = ReportingInteractionMode.DIALOG,
                resDialogIcon = android.R.drawable.stat_notify_error,
                resDialogTitle = R.string.acra_dialog_title,
                resDialogText = R.string.acra_dialog_text,
                resDialogPositiveButtonText = R.string.acra_send_mail,
                resDialogCommentPrompt = R.string.acra_dialog_comment_prompt)
public class KandroidApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constants.TAG, "Start ACRA");
        ACRA.init(this);
    }
}
