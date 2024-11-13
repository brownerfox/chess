package client;

import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import requests.CreateUserRequest;
import requests.JoinGameRequest;
import requests.LogInRequest;
import results.*;

import java.io.IOException;
import java.util.*;

public class ServerFacade {
    private final String serverUrl;
    String authToken;
    HashSet<GameData> gameList;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    protected String getAuthToken() {
        return authToken;
    }

    protected void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public HashSet<GameData> getGameList() {
        gameList.addAll(listGames().games());

        return gameList;
    }

    public CreateUserResult createUser (CreateUserRequest user) {
        var path = "/user";
        var body = Map.of("username", user.username(), "password", user.password(), "email", user.email());
        var jsonBody = new Gson().toJson(body);

        CreateUserResult result = this.makeRequest("POST", path, jsonBody, CreateUserResult.class);
        setAuthToken(result.authToken());

        return result;
    }

    public LogInResult loginUser (LogInRequest loginRequest) {
        var path = "/session";
        var body = Map.of("username", loginRequest.username(), "password", loginRequest.password());
        var jsonBody = new Gson().toJson(body);

        LogInResult result = this.makeRequest("POST", path, jsonBody, LogInResult.class);
        setAuthToken(result.authToken());

        return result;
    }

    public LogOutResult logoutUser () {
        var path = "/session";

        LogOutResult result = this.makeRequest("DELETE", path, null, null);
        setAuthToken(null);

        return result;
    }

    public ListGamesResult listGames () {
        var path = "/game";

        return this.makeRequest("GET", path, null, ListGamesResult.class);
    }

    public CreateGameResult createGame (String gameName) {
        var path = "/game";
        var body = Map.of("gameName", gameName);
        var jsonBody = new Gson().toJson(body);

        return this.makeRequest("POST", path, jsonBody, CreateGameResult.class);
    }

    public JoinGameResult joinGame (String playerColor, int gameID) {
        var path = "/game";
        var body = Map.of("playerColor", playerColor, "gameID", gameID);
        var jsonBody = new Gson().toJson(body);

        this.makeRequest("PUT", path, jsonBody, null);
    }

    public void clear () {
        var path = "/db";
        setAuthToken(null);

        this.makeRequest("DELETE", path, null, null);
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
