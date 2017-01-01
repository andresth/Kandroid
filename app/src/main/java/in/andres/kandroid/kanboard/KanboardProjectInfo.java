package in.andres.kandroid.kanboard;

/**
 * Created by thomas on 31.12.16.
 */

public class KanboardProjectInfo {
    public final int ID;
    public final String Name;

    public KanboardProjectInfo(int id, String name) {
        ID = id;
        Name = name;
    }

    @Override
    public String toString() {
        return Name;
    }
}
