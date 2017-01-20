package in.andres.kandroid.kanboard;

import java.util.Hashtable;

/**
 * Created by Thomas Andres on 20.01.17.
 */

public interface OnGetProjectUsersListener {
    void onGetProjectUsers(boolean success, Hashtable<Integer, String> result);
}
