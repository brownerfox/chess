package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import requests.*;
import results.CreateUserResult;
import results.ErrorResult;
import service.ChessService;
import spark.*;

import javax.xml.crypto.Data;

public class Server {
    private final ChessService service = new ChessService(new MemoryDataAccess());

    public Server () {}

    public int run(int desiredPort) {
        System.out.println("Waiting on the system");

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        Spark.post("/user", this::createUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);


        // Register your endpoints and handle exceptions here.
        //is line initializes the server and can be removed once you have a functioning endpoint
        //Spark
        //Th.init();

        Spark.awaitInitialization();
        return Spark.port();
    }



    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


    private Object createUser(Request req, Response res) throws DataAccessException {
        System.out.println("Check #1");
        var user = new Gson().fromJson(req.body(), CreateUserRequest.class);

        if (user == null || user.username() == null || user.password() == null || user.email() == null) {
            res.status(400);
            return new Gson().toJson(new ErrorResult("Error: bad request"));
        }

        System.out.println(user.toString());

        try {
            CreateUserResult result = service.createUser(user);

            System.out.println(result.toString());

            return new Gson().toJson(result);
        } catch (DataAccessException ex) {
            res.status(403);
            return new Gson().toJson(new ErrorResult("Error: already taken"));
        }
    }

    private Object loginUser(Request req, Response res) throws DataAccessException {
        var loginRequest = new Gson().fromJson(req.body(), LogInRequest.class);

        try {
            return new Gson().toJson(service.loginUser(loginRequest.username(), loginRequest.password()));
        } catch (DataAccessException ex) {
            res.status(401);
            return new Gson().toJson(new ErrorResult("Error: unauthorized"));
        }
    }

    private Object logoutUser(Request req, Response res) throws DataAccessException {
        String authHeader = req.headers("Authorization");
        String authToken = authHeader.replace("Bearer ", "");
        //var authData = new Gson().fromJson(req.headers("Authorization"), LogOutRequest.class);
        try {
            return new Gson().toJson(service.logoutUser(authToken));
        } catch (DataAccessException ex) {
            res.status(401);
            return new Gson().toJson(new ErrorResult("Error: unauthorized"));
        }
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        String authHeader = req.headers("Authorization");
        String authToken = authHeader.replace("Bearer ", "");

        try {
            return new Gson().toJson(service.listGames(authToken));
        } catch (DataAccessException ex) {
            res.status(401);
            return new Gson().toJson(new ErrorResult("Error: unauthorized"));
        }
    }

    private Object createGame(Request req, Response res) throws  DataAccessException {
        var createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);

        String authHeader = req.headers("Authorization");
        String authToken = authHeader.replace("Bearer ", "");

        if (createGameRequest == null || authToken == null || createGameRequest.gameName() == null) {
            res.status(400);
            return new Gson().toJson(new ErrorResult("Error: bad request"));
        }

        try {
            return new Gson().toJson(service.createGame(createGameRequest.authToken(), createGameRequest.gameName()));
        } catch (DataAccessException ex) {
            res.status(401);
            return new Gson().toJson(new ErrorResult("Error: unauthorized"));
        }
    }

    private Object joinGame(Request req, Response res) throws  DataAccessException {
        var joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);

        String authHeader = req.headers("Authorization");
        String authToken = authHeader.replace("Bearer ", "");

        Integer gameID = joinGameRequest.gameID();

        if (joinGameRequest == null || authToken == null || joinGameRequest.playerColor() == null || gameID == null) {
            res.status(400);
            return new Gson().toJson(new ErrorResult("Error: bad request"));
        }

        try {
            return new Gson().toJson(service.joinGame(joinGameRequest.authToken(), joinGameRequest.playerColor(), joinGameRequest.gameID()));
        } catch (DataAccessException e) {
            res.status(401);
            return new Gson().toJson(new ErrorResult("Error: unauthorized"));
        }
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        return service.clear();
    }
}
