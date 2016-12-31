package in.andres.kandroid.kanboard;

public class KanboardTask {
    public final String command;
    public final String json;

    private KanboardTask(String command, String json) {
        this.command = command;
        this.json = json;
    }

    public static KanboardTask getMe() {
        return new KanboardTask("getMe", "{\"jsonrpc\": \"2.0\", \"method\": \"getMe\", \"id\": 1}");
    }
}
