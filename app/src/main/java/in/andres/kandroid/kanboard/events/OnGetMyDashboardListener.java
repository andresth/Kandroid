package in.andres.kandroid.kanboard.events;

import in.andres.kandroid.kanboard.KanboardDashboard;

/**
 * Created by Thomas Andres on 30.01.17.
 */

public interface OnGetMyDashboardListener {
    void onGetMyDashboard(boolean success, KanboardDashboard result);
}
