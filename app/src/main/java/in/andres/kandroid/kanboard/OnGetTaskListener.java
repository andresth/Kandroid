package in.andres.kandroid.kanboard;

/**
 * Created by Thomas Andres on 20.01.17.
 */

public interface OnGetTaskListener {
    void onGetTask(boolean success, KanboardTask result);
}
