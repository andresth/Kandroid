package in.andres.kandroid.kanboard.events;

import java.util.List;

import in.andres.kandroid.kanboard.KanboardComment;

public interface OnGetAllCommentsListener {
    void onGetAllComments(boolean success, List<KanboardComment> comments);
}
