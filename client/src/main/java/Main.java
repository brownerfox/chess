import chess.*;
import client.ServerFacade;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var port = 8080;
        var serverUrl = "http://localhost:" + port;
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();
    }
}