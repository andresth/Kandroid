package in.andres.kandroid.kanboard;

import java.util.List;

/**
 * Created by Thomas Andres on 22.01.17.
 */

public interface OnGetAllSubtasksListener {
    void onGetAllSubtasks(boolean success, List<KanboardSubtask> result);
}
