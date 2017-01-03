package in.andres.kandroid.kanboard;

import org.json.JSONObject;

public class KanboardResult {
    public final String Command;
    public final JSONObject Result;
    public final KanboardRequest Request;
    public final int ReturnCode;

    public KanboardResult(KanboardRequest request, JSONObject json, int returnCode) {
        Request = request;
        Command = "";
        Result = json;
        ReturnCode = returnCode;
    }
}
