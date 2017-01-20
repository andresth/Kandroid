package in.andres.kandroid.kanboard;

import java.util.List;

public interface OnGetAllCommentsListener {
    void onGetAllComments(boolean success, List<KanboardComment> comments);
}
