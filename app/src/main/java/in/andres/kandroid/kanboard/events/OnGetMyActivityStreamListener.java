package in.andres.kandroid.kanboard.events;

import java.util.List;

import in.andres.kandroid.kanboard.KanboardActivity;

/**
 * Created by Thomas Andres on 30.01.17.
 */

public interface OnGetMyActivityStreamListener {
    void onGetMyActivityStream(boolean success, List<KanboardActivity> result);
}
