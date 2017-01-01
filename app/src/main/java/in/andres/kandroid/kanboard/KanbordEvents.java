package in.andres.kandroid.kanboard;

import org.json.JSONObject;

import java.util.List;

public interface KanbordEvents {
    void onGetMe(boolean success, KanboardUserInfo userInfo);
    void onGetMyProjectsList(boolean success, List<KanboardProjectInfo> projects);
    void onDebug(boolean success, String message);
}
