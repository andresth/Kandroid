package in.andres.kandroid.kanboard.events;

/**
 * Created by thomas on 24.02.17.
 */

public interface OnCreateTaskListener {
    void onCreateTask(boolean success, Integer taskid);
}
