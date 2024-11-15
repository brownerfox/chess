package client;

import chess.ChessGame;
import model.GameData;
import requests.CreateUserRequest;
import requests.JoinGameRequest;
import requests.LogInRequest;
import results.CreateGameResult;
import results.ListGamesResult;
import exception.ResponseException;
import ui.BoardCreator;
import ui.State;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ChessClient {
    private final ServerFacade server;
    private static final String SUCCESS_LOGOUT_MESSAGE = "You've successfully logged out!";
    private State state = State.SIGNEDOUT;
    BoardCreator boardCreator = new BoardCreator(new ChessGame());

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
                case "list" -> printGames();
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
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

    public ArrayList<GameData> listGames() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You need to sign in!");
        } else {
            ListGamesResult listGames = server.listGames();

            return new ArrayList<>(listGames.games());
        }
    }

    public String printGames() throws ResponseException {
        StringBuilder result = new StringBuilder();
        ArrayList<GameData> games = listGames();
        int i = 1;

        if (games.isEmpty()) {
            result.append("You need to create a game first!");
        }

        for (GameData game : games) {
            String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
            String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
            result.append(String.format("Game ID: %d Game Name: %s White Player: %s Black Player: %s %n", i, game.gameName(), whiteUser, blackUser));
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
            try {
                CreateGameResult gameResult = server.createGame(params[0]);
                int listGameID = findGameIndex(gameResult.gameID());
                return String.format("Created game with ID %x!", listGameID + 1);
            } catch (ResponseException e) {
                e.setMessage("Couldn't create game!");
                return e.getMessage();
            }
        }
    }

    public String joinGame (String... params) throws ResponseException {
        StringBuilder output = new StringBuilder();

        if (state == State.SIGNEDOUT) {
            output.append("You need to sign in!");
            return output.toString();
        }
        if (params.length != 2 || !params[0].matches("\\d+") || !params[1].toUpperCase().matches("WHITE|BLACK")) {
            if (params.length != 2) {
                output.append("You need to specify a game ID and a team color!");
                return output.toString();
            } else if (!params[0].matches("\\d+")) {
                output.append("Enter a valid game ID!");
                return output.toString();
            } else {
                output.append("Enter a valid team color: WHITE|BLACK!");
                return output.toString();
            }
        }
        int gameID = Integer.parseInt(params[0]);

        JoinGameRequest joinGameRequest = new JoinGameRequest(server.getAuthToken(), params[1], gameID);

        ListGamesResult listGames = server.listGames();

        if (listGames.games().isEmpty() || listGames.games().size() <= gameID) {
            if (listGames.games().isEmpty()) {
                output.append("Create a game first!");
                return output.toString();
            }
            if (listGames.games().size() < gameID) {
                output.append("Enter a valid game ID!");
                return output.toString();
            }
        }
        if (findGameIndex(gameID) != -1) {
            String result = server.joinGame(joinGameRequest);
            if (Objects.equals(result, "Couldn't join game!")) {
                output.append(result);
                return output.toString();
            } else {
                boardCreator.printBoard(ChessGame.TeamColor.WHITE);
                boardCreator.printBoard(ChessGame.TeamColor.BLACK);
                output.append(result);
                return output.toString();
            }
        } else {
            output.append("Game does not exist!");
            return output.toString();
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

        JoinGameRequest joinGameRequest = new JoinGameRequest(server.getAuthToken(), null, gameID);

        ListGamesResult listGames = server.listGames();

        if (listGames.games().isEmpty() || listGames.games().size() <= gameID) {
            if (listGames.games().isEmpty()) {
                return ("Create a game first!");
            }
            if (listGames.games().size() < gameID) {
                return ("Enter a valid game ID!");
            }
        }
        if (findGameIndex(gameID) != -1) {
            boardCreator.printBoard(ChessGame.TeamColor.WHITE);
            boardCreator.printBoard(ChessGame.TeamColor.BLACK);
            return "Observer pre gameplay";
            //return server.joinGame(joinGameRequest);
        } else {
            return ("Game does not exist!");
        }
    }

    public String clear () {
        state = State.SIGNEDOUT;
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
