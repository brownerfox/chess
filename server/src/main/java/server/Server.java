package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requests.ListGamesRequest;
import requests.LogOutRequest;
import requests.LogInRequest;
import requests.CreateUserRequest;
import results.CreateUserResult;
import results.LogInResult;
import service.ChessService;
import spark.*;

public class Server {
    private final ChessService service;

    public Server (ChessService service) {
        this.service = service;
    }

    public int run(int desiredPort) {
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
        var user = new Gson().fromJson(req.body(), CreateUserRequest.class);
        try {
            CreateUserResult userResult = service.createUser(user);
            return new Gson().toJson(userResult);
        } catch (DataAccessException ex) {
            res.status(400);
            return ex.getMessage();
        }
    }

    private Object loginUser(Request req, Response res) throws DataAccessException {
        var loginRequest = new Gson().fromJson(req.body(), LogInRequest.class);

        LogInResult loginResult = service.loginUser(loginRequest.username(), loginRequest.password());

        return new Gson().toJson(service.loginUser(loginRequest.username(), loginRequest.password()));
    }

    private Object logoutUser(Request req, Response res) throws DataAccessException {
        var authData = new Gson().fromJson(req.body(), LogOutRequest.class);

        return new Gson().toJson(service.logoutUser(authData.authToken()));
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        var authData = new Gson().fromJson(req.body(), ListGamesRequest.class);

        return new Gson().toJson(service.listGames(authData.authToken()));
    }

    private Object createGame(Request req, Response res) throws  DataAccessException {
        return new Gson().toJson(service.createGame(new Gson().fromJson(req.body(), )));
    }

    private Object joinGame(Request req, Response res) throws  DataAccessException {
        return "";
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        return "";
    }
}
