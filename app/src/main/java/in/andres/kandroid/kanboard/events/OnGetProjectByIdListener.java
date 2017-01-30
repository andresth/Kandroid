package in.andres.kandroid.kanboard.events;

import in.andres.kandroid.kanboard.KanboardProject;

/**
 * Created by Thomas Andres on 30.01.17.
 */

public interface OnGetProjectByIdListener {
    void onGetProjectById(boolean success, KanboardProject result);
}
