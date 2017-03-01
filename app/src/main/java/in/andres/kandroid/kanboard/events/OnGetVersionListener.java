package in.andres.kandroid.kanboard.events;

/**
 * Created by Thomas Andres on 01.03.17.
 */

public interface OnGetVersionListener {
    void onGetVersion(boolean success, int[] version, String tag);
}
