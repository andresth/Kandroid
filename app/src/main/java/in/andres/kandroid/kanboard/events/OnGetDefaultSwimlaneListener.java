package in.andres.kandroid.kanboard.events;

/**
 * Created by Thomas Andres on 21.01.17.
 */

public interface OnGetDefaultSwimlaneListener {
    void onGetDefaultSwimlane(boolean success, String name, boolean isActive);
}
