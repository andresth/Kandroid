package in.andres.kandroid.kanboard;

public class KanboardTask {
    public final String Command;
    public final String JSON;

    private KanboardTask(String command, String json) {
        this.Command = command;
        this.JSON = json;
    }

    public static KanboardTask getMe() {
        return new KanboardTask("getMe", "{\"jsonrpc\": \"2.0\", \"method\": \"getMe\", \"id\": 1}");
    }

    public static KanboardTask getMyProjectsList() {
        return new KanboardTask("getMyProjectsList", "{\"jsonrpc\": \"2.0\", \"method\": \"getMyProjectsList\", \"id\": 1}");
    }
}
