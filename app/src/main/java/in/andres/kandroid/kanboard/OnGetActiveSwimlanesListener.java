package in.andres.kandroid.kanboard;

import java.util.Hashtable;

/**
 * Created by Thomas Andres on 21.01.17.
 */

public interface OnGetActiveSwimlanesListener {
    void onGetActiveSwimlanes(boolean success, Hashtable<Integer, KanboardSwimlane> result);
}
