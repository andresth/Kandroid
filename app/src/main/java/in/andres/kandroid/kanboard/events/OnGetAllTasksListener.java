package in.andres.kandroid.kanboard.events;

import java.util.List;

import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Created by Thomas Andres on 30.01.17.
 */

public interface OnGetAllTasksListener {
    void onGetAllTasks(boolean success, int status, List<KanboardTask> result);
}
