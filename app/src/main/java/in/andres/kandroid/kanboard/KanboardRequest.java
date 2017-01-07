package in.andres.kandroid.kanboard;

public class KanboardRequest {
    public final String Command;
    public final String[] JSON;

    private KanboardRequest(String command, String[] json) {
        this.Command = command;
        this.JSON = json;
    }

    public static KanboardRequest getMe() {
        return new KanboardRequest("getMe", new String[] {"{\"jsonrpc\": \"2.0\", \"method\": \"getMe\", \"id\": 1}"});
    }

    public static KanboardRequest getMyProjectsList() {
        return new KanboardRequest("getMyProjectsList", new String[] {"{\"jsonrpc\": \"2.0\", \"method\": \"getMyProjectsList\", \"id\": 1}"});
    }

    public static KanboardRequest getMyDashboard() {
        return new KanboardRequest("getMyDashboard", new  String[] {"{\"jsonrpc\": \"2.0\", \"method\": \"getMyDashboard\", \"id\": 1}"});
    }

    public static KanboardRequest getMyOverdueTasks() {
        return new KanboardRequest("getMyOverdueTasks", new String[] {"{\"jsonrpc\": \"2.0\", \"method\": \"getMyDashboard\", \"id\": 1}"});
    }
}
