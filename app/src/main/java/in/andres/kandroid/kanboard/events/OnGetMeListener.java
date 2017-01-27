package in.andres.kandroid.kanboard.events;

import in.andres.kandroid.kanboard.KanboardUserInfo;

/**
 * Created by Thomas Andres on 24.01.17.
 */

public interface OnGetMeListener {
    void onGetMe(boolean success, KanboardUserInfo result);

}
