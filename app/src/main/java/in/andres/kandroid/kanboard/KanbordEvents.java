package in.andres.kandroid.kanboard;

import org.json.JSONObject;

public interface KanbordEvents {
    public void onGetMe(boolean succsess, KanboardUserInfo userInfo);
}
