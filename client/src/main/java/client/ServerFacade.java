package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;

import java.io.IOException;
import java.util.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) throws Exception {
        serverUrl = url;
    }

    public CreateUserResult createUser (CreateUserRequest user) {
        var path = "/user";

        return this.makeRequest("POST", path, user, CreateUserResult.class);
    }

    public LoginResult loginUser (LoginRequest loginRequest) {
        var path = "/session";

        return this.makeRequest("POST", path, loginRequest, LoginResult.class);
    }

    public LogoutResult logoutUser (LogoutRequest logoutRequest) {
        var path = "/session";

        return this.makeRequest("DELETE", path, null, null);
    }

    public ListGameResult listGames (ListGameRequest listGameRequest) {
        var path = "/game";

        return this.makeRequest("GET", path, null, ListGameResult.class);
    }

    public CreateGameResult createGame (CreateGameRequest createGameRequest) {
        var path = "/game";

        return this.makeRequest("POST", path, createGameRequest, CreateGameResult.class);
    }

    public JoinGameResult joinGame (JoinGameRequest joinGameRequest) {
        var path = "/game";

        return this.makeRequest("PUT", path, joinGameRequest, null);
    }

    public clear () {
        var path = "/db";

        return this.makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

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
