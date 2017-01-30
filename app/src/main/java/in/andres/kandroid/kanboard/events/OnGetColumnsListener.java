package in.andres.kandroid.kanboard.events;

import java.util.List;

import in.andres.kandroid.kanboard.KanboardColumn;

/**
 * Created by Thomas Andres on 30.01.17.
 */

public interface OnGetColumnsListener {
    void onGetColumns(boolean success, List<KanboardColumn> result);
}
