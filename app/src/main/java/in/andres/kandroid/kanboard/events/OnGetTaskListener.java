package in.andres.kandroid.kanboard.events;

import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Created by Thomas Andres on 20.01.17.
 */

public interface OnGetTaskListener {
    void onGetTask(boolean success, KanboardTask result);
}
