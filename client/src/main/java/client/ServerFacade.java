package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;

import java.io.IOException;
import java.util.*;

public class ServerFacade {
    private final String serverUrl;
    String authToken;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    protected String getAuthToken() {
        return authToken;
    }

    protected void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public CreateUserResult createUser (CreateUserRequest user) {
        var path = "/user";
        var body = Map.of("username", user.username(), "password", user.password(), "email", user.email());
        var jsonBody = new Gson().toJson(body);

        CreateUserResult result = this.makeRequest("POST", path, jsonBody, CreateUserResult.class);
        setAuthToken(result.authToken);

        return result;
    }

    public LoginResult loginUser (LoginRequest loginRequest) {
        var path = "/session";
        var body = Map.of("username", loginRequest.username(), "password", loginRequest.password());
        var jsonBody = new Gson().toJson(body);

        LoginResult result = this.makeRequest("POST", path, jsonBody, LoginResult.class);
        setAuthToken(result.authToken);

        return result;
    }

    public LogoutResult logoutUser () {
        var path = "/session";

        LogoutResult result = this.makeRequest("DELETE", path, null, null);
        setAuthToken(null);

        return result;
    }

    public ListGameResult listGames () {
        var path = "/game";

        return this.makeRequest("GET", path, null, ListGameResult.class);
    }

    public CreateGameResult createGame (CreateGameRequest createGameRequest) {
        var path = "/game";
        var body = Map.of("gameName", createGameRequest.gameName());
        var jsonBody = new Gson().toJson(body);

        return this.makeRequest("POST", path, jsonBody, CreateGameResult.class);
    }

    public JoinGameResult joinGame (JoinGameRequest joinGameRequest) {
        var path = "/game";
        var body = Map.of("playerColor", joinGameRequest.playerColor(), "gameID", joinGameRequest.gameID());
        var jsonBody = new Gson().toJson(body);

        return this.makeRequest("PUT", path, jsonBody, null);
    }

    public clear () {
        var path = "/db";
        setAuthToken(null);

        return this.makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (getAuthToken() != null) {
                http.addRequestProperty("authorization", facade.getAuthToken());
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
