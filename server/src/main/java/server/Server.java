package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySQLDataAccess;
import requests.*;
import results.CreateUserResult;
import results.ErrorResult;
import service.BadGameIDException;
import service.ChessService;
import service.ServiceException;
import spark.*;

import java.sql.SQLException;

public class Server {

    private final ChessService service = new ChessService(new MySQLDataAccess());

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


    private Object createUser(Request req, Response res) throws Exception {
        var user = new Gson().fromJson(req.body(), CreateUserRequest.class);

        if (user == null || user.username() == null || user.password() == null || user.email() == null) {
            res.status(400);
            return new Gson().toJson(new ErrorResult("Error: bad request"));
        }

        try {
            CreateUserResult result = service.createUser(user);

            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            res.status(403);
            return new Gson().toJson(new ErrorResult("Error: already taken"));
        }
    }

    private Object loginUser(Request req, Response res) {
        var loginRequest = new Gson().fromJson(req.body(), LogInRequest.class);

        try {
            return new Gson().toJson(service.loginUser(loginRequest.username(), loginRequest.password()));
        } catch (DataAccessException ex) {
            res.status(401);
            return new Gson().toJson(new ErrorResult("Error: unauthorized"));
        }
    }

    private Object logoutUser(Request req, Response res) {
        String authToken = req.headers("Authorization");

        try {
            return new Gson().toJson(service.logoutUser(authToken));
        } catch (DataAccessException ex) {
            res.status(401);
            return new Gson().toJson(new ErrorResult("Error: unauthorized"));
        }
    }

    private Object listGames(Request req, Response res) throws ServiceException {
        String authToken = req.headers("Authorization");

        try {
            return new Gson().toJson(service.listGames(authToken));
        } catch (DataAccessException ex) {
            res.status(401);
            return new Gson().toJson(new ErrorResult("Error: unauthorized"));
        }
    }

    private Object createGame(Request req, Response res) throws  ServiceException {
        var createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        String authToken = req.headers("Authorization");

        if (createGameRequest == null || authToken == null || createGameRequest.gameName() == null) {
            res.status(400);
            return new Gson().toJson(new ErrorResult("Error: bad request"));
        }

        try {
            return new Gson().toJson(service.createGame(authToken, createGameRequest.gameName()));
        } catch (DataAccessException ex) {
            res.status(401);
            return new Gson().toJson(new ErrorResult("Error: unauthorized"));
        }
    }

    private Object joinGame(Request req, Response res) throws SQLException {
        var joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);

        String authToken = req.headers("Authorization");

        Integer gameID = joinGameRequest.gameID();

        if (joinGameRequest == null || authToken == null ||  gameID == null) {
            res.status(400);
            return new Gson().toJson(new ErrorResult("Error: bad request"));
        }

        try {
            return new Gson().toJson(service.joinGame(authToken, joinGameRequest.playerColor(), joinGameRequest.gameID()));
        } catch (BadGameIDException e) {
            res.status(400);
            return new Gson().toJson(new ErrorResult("Error: bad request"));
        } catch (DataAccessException e) {
            res.status(401);
            return new Gson().toJson(new ErrorResult("Error: unauthorized"));
        } catch (ServiceException e) {
            res.status(403);
            return new Gson().toJson(new ErrorResult("Error: already taken"));
        }
    }

    private Object clear(Request req, Response res) throws DataAccessException, ServiceException {
        return new Gson().toJson(service.clear());
    }
}
