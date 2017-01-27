package in.andres.kandroid.kanboard.events;

import in.andres.kandroid.kanboard.KanboardCategory;

/**
 * Created by Thomas Andres on 20.01.17.
 */

public interface OnGetCategoryListener {
    void onGetCategory(boolean success, KanboardCategory result);
}
