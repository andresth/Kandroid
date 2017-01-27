package in.andres.kandroid.kanboard.events;

import in.andres.kandroid.kanboard.KanboardSwimlane;

/**
 * Created by Thomas Andres on 20.01.17.
 */

public interface OnGetSwimlaneListener {
    void onGetSwimlane(boolean success, KanboardSwimlane result);
}
