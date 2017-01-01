package in.andres.kandroid.kanboard;

import org.json.JSONObject;

public class KanboardResult {
    public final String Command;
    public final JSONObject JSON;
    public final int ReturnCode;

    public KanboardResult(String command, JSONObject json, int returnCode) {
        this.Command = command;
        this.JSON = json;
        this.ReturnCode = returnCode;
    }
}
