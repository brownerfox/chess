package client;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import requests.CreateUserRequest;
import requests.JoinGameRequest;
import requests.LogInRequest;
import results.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class ServerFacade {
    private final String serverUrl;
    String userName;
    String authToken;
    HashSet<GameData> gameList;
    ChessGame game;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    protected String getUserName() {return userName;}

    protected void setUserName(String userName) {this.userName = userName;}

    protected String getAuthToken() {
        return authToken;
    }

    protected void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String createUser (CreateUserRequest user) {
        if (user.username() == null || user.password() == null || user.email() == null) {
            return ("You need to insert your username, password, and email!");
        }
        StringBuilder output = new StringBuilder();
        var path = "/user";
        var body = Map.of("username", user.username(), "password", user.password(), "email", user.email());

        try {
            CreateUserResult result = this.makeRequest("POST", path, body, CreateUserResult.class);
            setAuthToken(result.authToken());
            setUserName(user.username());

            output.append(String.format("You signed in as %s!", getUserName()));
            return output.toString();

        } catch (ResponseException e) {
            output.append("Failed to create user!");
            return output.toString();
        }
    }

    public String logInUser (LogInRequest logInRequest) {
        if (logInRequest.username() == null || logInRequest.password() == null) {
            return "You need to insert a username and a password!";
        }
        var path = "/session";
        var body = Map.of("username", logInRequest.username(), "password", logInRequest.password());

        try {
            LogInResult result = makeRequest("POST", path, body, LogInResult.class);
            setAuthToken(result.authToken());

            return String.format("You signed in as %s!", logInRequest.username());
        } catch (ResponseException e) {
            e.setMessage("Failed to login to user!");
            return (e.getMessage());
        }
    }

    public String logOutUser () {
        var path = "/session";

        try {
            this.makeRequest("DELETE", path, null, null);
            setAuthToken(null);
            return ("You've successfully logged out!");
        } catch (ResponseException e) {
            e.setMessage("Failed to logout user!");
            return(e.getMessage());
        }
    }

    public ListGamesResult listGames () throws ResponseException {
        var path = "/game";

        return this.makeRequest("GET", path, null, ListGamesResult.class);
    }

    public CreateGameResult createGame (String gameName) throws ResponseException {
        var path = "/game";
        var body = Map.of("gameName", gameName);

        return this.makeRequest("POST", path, body, CreateGameResult.class);
    }

    public String joinGame (JoinGameRequest joinGameRequest) {
        var path = "/game";
        var body = Map.of("playerColor", joinGameRequest.playerColor(), "gameID", joinGameRequest.gameID());

        if (joinGameRequest.playerColor() != null) {
            try {
                this.makeRequest("PUT", path, body, null);
                return String.format("Game joined as %s player!", getUserName());
            } catch (ResponseException e) {
                e.setMessage("Couldn't join game!");
                return e.getMessage();
            }
        } else {
            try {
                this.makeRequest("PUT", path, body, null);
                return ("Game joined as an observer!");
            } catch (ResponseException e) {
                e.setMessage("Couldn't join game!");
                return e.getMessage();
            }
        }
    }

    public String clear () {
        var path = "/db";
        setAuthToken(null);
        try {
            this.makeRequest("DELETE", path, null, null);
            return ("Successfully cleared out the database!");
        } catch (ResponseException e) {
            e.setMessage("Unable to clear the database!");
            return e.getMessage();
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (getAuthToken() != null) {
                http.addRequestProperty("authorization", getAuthToken());
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
