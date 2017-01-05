package in.andres.kandroid.kanboard;

import org.json.JSONObject;

/**
 * Created by Thomas Andres on 03.01.17.
 */

public class KanboardError {
    public final int Code;
    public final String Message;
    public final KanboardRequest Request;
    public final int HTTPReturnCode;

    public KanboardError(KanboardRequest request, JSONObject json, int httpreturcode) {
        if (json != null) {
            Code = json.optInt("code");
            Message = json.optString("message");
        } else {
            Code = -2;
            Message = "Null Object";
        }
        Request = request;
        HTTPReturnCode = httpreturcode;
    }
}
