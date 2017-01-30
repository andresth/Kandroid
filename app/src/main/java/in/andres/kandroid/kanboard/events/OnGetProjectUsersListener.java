package in.andres.kandroid.kanboard.events;

import java.util.Dictionary;

/**
 * Created by Thomas Andres on 20.01.17.
 */

public interface OnGetProjectUsersListener {
    void onGetProjectUsers(boolean success, Dictionary<Integer, String> result);
}
