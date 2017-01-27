package in.andres.kandroid.kanboard.events;

import java.util.List;

import in.andres.kandroid.kanboard.KanboardSubtask;

/**
 * Created by Thomas Andres on 22.01.17.
 */

public interface OnGetAllSubtasksListener {
    void onGetAllSubtasks(boolean success, List<KanboardSubtask> result);
}
