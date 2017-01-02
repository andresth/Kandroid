package in.andres.kandroid.kanboard;

public class KanboardRequest {
    public final String Command;
    public final String JSON;

    private KanboardRequest(String command, String json) {
        this.Command = command;
        this.JSON = json;
    }

    public static KanboardRequest getMe() {
        return new KanboardRequest("getMe", "{\"jsonrpc\": \"2.0\", \"method\": \"getMe\", \"id\": 1}");
    }

    public static KanboardRequest getMyProjectsList() {
        return new KanboardRequest("getMyProjectsList", "{\"jsonrpc\": \"2.0\", \"method\": \"getMyProjectsList\", \"id\": 1}");
    }

    public static KanboardRequest getMyDashboard() {
        return new KanboardRequest("getMyDashboard", "{\"jsonrpc\": \"2.0\", \"method\": \"getMyDashboard\", \"id\": 1}");
    }
}
