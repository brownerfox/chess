package ui;

import client.ServerFacade;
import org.eclipse.jetty.http.MetaData;
import ui.State;

import java.util.Arrays;

public class ChessClient {
    private String userName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public ChessClient (String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> createUser(params);
                case "login" -> loginUser(params);
                case "logout" -> logoutUser();
                case "gamelist" -> listGames();
                case "createagame" -> createGame(params);
                case "joingame" -> joinGame(params);
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createUser(String... params) throws ResponseException {
        if (params.length == 3) {
            state = State.SIGNEDIN;
            userName = String.join("-", params);
            return String.format("You signed in as %s.", userName);
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String loginUser(String... params) throws ResponseException {
        if (params.length == 2) {
            state = State.SIGNEDIN;
            userName = String.join("-", params);
            return String.format("You signed in as %s.", userName);
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String logoutUser () throws ResponseException {

    }
}
