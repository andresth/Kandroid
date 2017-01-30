package in.andres.kandroid.kanboard.events;

import java.util.List;

import in.andres.kandroid.kanboard.KanboardSwimlane;

/**
 * Created by Thomas Andres on 21.01.17.
 */

public interface OnGetActiveSwimlanesListener {
    void onGetActiveSwimlanes(boolean success, List<KanboardSwimlane> result);
}
