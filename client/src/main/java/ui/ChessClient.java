package ui;

import client.ServerFacade;
import model.GameData;
import requests.CreateUserRequest;
import requests.LogInRequest;
import results.CreateGameResult;
import results.ListGamesResult;
import exception.ResponseException;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class ChessClient {
    private final ServerFacade server;
    private static final String SUCCESS_LOGOUT_MESSAGE = "You've successfully logged out!";
    private State state = State.SIGNEDOUT;

    public ChessClient (String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public State getState() {
        return state;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> createUser(params);
                case "login" -> logInUser(params);
                case "logout" -> logOutUser();
                case "gamelist" -> printGames();
                case "createagame" -> createGame(params);
                case "joingame" -> joinGame(params);
                case "observegame" -> observeGame(params);
                case "clear" -> clear();
                default -> printHelpMenu();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createUser(String... params) {
        if (params.length != 3) {
            return ("You need to insert your username, password, and email!");
        } else {
            CreateUserRequest user = new CreateUserRequest(params[0], params[1], params[2]);
            String result = server.createUser(user);
            if (Objects.equals(result, String.format("You signed in as %s!", user.username()))) {
                state = State.SIGNEDIN;
            }
            return result;
        }
    }

    public String logInUser(String... params) {
        if (params.length != 2) {
            return ("You need to insert your username and password!");
        } else {
            LogInRequest logInRequest = new LogInRequest(params[0], params[1]);
            String result = server.logInUser(logInRequest);
            if (Objects.equals(result, String.format("You signed in as %s!", logInRequest.username()))) {
                state = State.SIGNEDIN;
            }
            return result;
        }
    }

    public String logOutUser () {
        if (state == State.SIGNEDOUT) {
            return ("You need to sign in!");
        }
        String result = server.logOutUser();

        if (Objects.equals(result, SUCCESS_LOGOUT_MESSAGE)) {
            state = State.SIGNEDOUT;
        }

        return result;
    }

    public HashSet<GameData> listGames() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You need to sign in!");
        } else {
            ListGamesResult listGames = server.listGames();
            return listGames.games();
        }
    }

    public String printGames() throws ResponseException {
        StringBuilder result = new StringBuilder();
        HashSet<GameData> games = listGames();
        int i = 1;

        for (GameData game : games) {
            String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
            String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
            result.append(String.format("Game ID: %d Game Name: %s White User: %s Black User %s %n", i, game.gameName(), whiteUser, blackUser));
            i++;
        }
        return result.toString();
    }

    public String createGame(String... params) throws ResponseException {
        if (state == State.SIGNEDOUT) {
            return ("You need to sign in!");
        }
        if (params.length != 1) {
            return ("Only include the name of the game you want to create!");
        } else {
            CreateGameResult gameResult = server.createGame(params[0]);
            int listGameID = findGameIndex(gameResult.gameID());
            return String.format("Created game with ID %x!", listGameID + 1);
        }
    }

    public String joinGame (String... params) throws ResponseException {
        if (state == State.SIGNEDOUT) {
            return ("You need to sign in!");
        }
        if (params.length != 2 || !params[0].matches("\\d+") || !params[1].toUpperCase().matches("WHITE|BLACK")) {
            return ("You need to specify a game ID and a team color!");
        }
        int gameID = Integer.parseInt(params[0]);

        if (server.listGames().games().isEmpty() || server.listGames().games().size() <= gameID) {
            if (server.listGames().games().isEmpty()) {
                return ("Create a game first!");
            }
            if (server.listGames().games().size() <= gameID) {
                return ("Enter a valid game ID!");
            }
        }
        if (findGameIndex(gameID) != -1) {
            return server.joinGame(params[1], gameID);
        } else {
            return ("Game does not exist!");
        }
    }

    public String observeGame (String[] params) throws ResponseException {
        if (state == State.SIGNEDOUT) {
            return ("You need to sign in!");
        }
        if (params.length != 1 || !params[0].matches("\\d+")) {
            return ("You need to specify a game ID!");
        }

        int gameID = Integer.parseInt(params[0]);

        if (server.listGames().games().isEmpty() || server.listGames().games().size() <= gameID) {
            if (server.listGames().games().isEmpty()) {
                return ("Create a game first!");
            }
            if (server.listGames().games().size() <= gameID) {
                return ("Enter a valid game ID!");
            }
        }
        if (findGameIndex(gameID) != -1) {
            return server.joinGame(null, gameID);
        } else {
            return ("Game does not exist!");
        }
    }

    public String clear () {
        return server.clear();
    }

    public int findGameIndex(int gameID) throws ResponseException {
        int index = 0;
        for (GameData game : server.listGames().games()) {
            if (game.gameID() == gameID) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public String printHelpMenu() {
        if (state != State.SIGNEDIN) {
            return """
            register <USERNAME> <PASSWORD> <EMAIL> - create a new user
            login <USERNAME> <PASSWORD> - login to an existing user
            quit - stop playing
            help - show this menu
            """;
        } else {
            return """
            create <NAME> - create a new game
            list - list all games
            join <ID> [WHITE|BLACK] - join a game as color
            observe <ID> - observe a game
            logout - log out of current user
            quit - stop playing
            help - show this menu
            """;
        }
    }

}
