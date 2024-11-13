package ui;

import client.ServerFacade;
import org.eclipse.jetty.http.MetaData;
import ui.State;
import exception.ResponseException;

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
                case "help" -> printHelpMenu();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createUser(String... params) throws ResponseException {
        if (params.length == 3) {
            state = State.SIGNEDIN;
            userName = params[0];
            return String.format("You signed in as %s.", userName);
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String loginUser(String... params) {
        if (params.length != 2) {
            return ("You need to insert your username and password");
        } else {
            state = State.SIGNEDIN;
            userName = params[0];
            return String.format("You signed in as %s.", userName);
        }
    }

    public String logoutUser () {
        if (state == State.SIGNEDOUT) {
            return ("You need to sign in!");
        }
        try {
            server.logoutUser();
            state = State.SIGNEDOUT;
        } catch (Exception e) {
            return e.getMessage();
        }
        return ("You've successfully logged out.");
    }

    public String listGames() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            return ("You need to sign in!");
        }
        try {
            Array<GameData> GameList = server.listGames();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String createGame(String... params) throws ResponseException {
        if (state == State.SIGNEDOUT) {
            return ("You need to sign in!");
        }
        if (params.length != 1) {
            return ("Include only the name of the game you want to create!");
        } else {
            CreateGameResult gameResult = server.createGame(params[0]);
            int listGameID = findGameIndex(gameResult.gameID());
            return String.format("Created game with ID %x.", listGameID + 1);
        }
    }

    public String joinGame (String... params) throws ResponseException {
        if (state == State.SIGNEDOUT) {
            return ("You need to sign in!");
        }
        if (params.length != )
    }

    public int findGameIndex(Int gameID) {
        int index = 0;
        for (GameData game : server.getGameList()) {
            if (game.gameID() == gameID) {
                return index;
            }
            index++;
        }
        return -1;
    }


}
