package server;

import dataaccess.DataAccessException;
import spark.*;

public class Server {

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
        Spark.exception(DataAccessException.class, this::exceptionHandler);


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

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.StatusCode());
    }

    private Object createUser(Request req, Response res) throws DataAccessException {
        return authToken;
    }

    private Object loginUser(Request req, Response res) throws DataAccessException {
        return authToken;
    }

    private Object logoutUser(Request req, Response res) throws DataAccessException {
        return "";
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        return gameList;
    }

    private Object createGame(Request req, Response res) throws  DataAccessException {
        return gameData;
    }

    private Object joinGame(Request req, Response res) throws  DataAccessException {
        return "";
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        return "";
    }
}