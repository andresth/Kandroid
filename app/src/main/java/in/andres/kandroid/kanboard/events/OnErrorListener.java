package in.andres.kandroid.kanboard.events;

import in.andres.kandroid.kanboard.KanboardError;

/**
 * Created by Thomas Andres on 01.03.17.
 */

public interface OnErrorListener {
    void onError(KanboardError error);
}
