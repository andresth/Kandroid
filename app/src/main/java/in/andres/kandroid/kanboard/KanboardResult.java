package in.andres.kandroid.kanboard;

public class KanboardResult {
    public final String command;
    public final String json;

    public KanboardResult(String command, String json) {
        this.command = command;
        this.json = json;
    }
}
