package in.andres.kandroid.kanboard.events;

/**
 * Created by thomas on 06.02.17.
 */

public interface OnSubtaskTimetrackingListener {
    void onSubtaskTimetracking(boolean result, double time);
}
