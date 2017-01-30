package in.andres.kandroid.kanboard.events;

import java.util.List;

import in.andres.kandroid.kanboard.KanboardCategory;

/**
 * Created by Thomas Andres on 30.01.17.
 */

public interface OnGetAllCategoriesListener {
    void onGetAllCategories(boolean success, List<KanboardCategory> result);
}
