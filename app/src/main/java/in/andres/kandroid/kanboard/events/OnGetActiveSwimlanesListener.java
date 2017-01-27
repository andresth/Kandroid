package in.andres.kandroid.kanboard.events;

import java.util.Hashtable;

import in.andres.kandroid.kanboard.KanboardSwimlane;

/**
 * Created by Thomas Andres on 21.01.17.
 */

public interface OnGetActiveSwimlanesListener {
    void onGetActiveSwimlanes(boolean success, Hashtable<Integer, KanboardSwimlane> result);
}
