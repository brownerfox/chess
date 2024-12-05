package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import results.ErrorResult;
import service.BadGameIDException;
import service.ServiceException;
import websocket.commands.JoinGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    DataAccess dataAccess;
    AuthData authData;
    GameData gameData;

    public WebSocketHandler (DataAccess dataAccess) {
       this.dataAccess = dataAccess;
    }

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        connections.add(0, session);
    }

    @OnWebSocketClose
    public void onClose(Session session) {
        connections.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {

        if (message.contains("\"commandType\":\"JOIN_PLAYER\"")) {
            JoinGameCommand action = new Gson().fromJson(message,JoinGameCommand.class);
            setData(session, action.authToken(), action.gameID());
            joinPlayer(session, action);
        } else if (message.contains("\"commandType\":\"MAKE_MOVE\"")) {
            MakeMoveCommand action = new Gson().fromJson(message, MakeMoveCommand.class);
            setData(session, action.authToken(), action.gameID());
            makeMove(session, action);
        } else {
            UserGameCommand action = new Gson().fromJson(message,UserGameCommand.class);
            setData(session, action.getAuthToken(), action.getGameID());
            determineAction(session, action);
        }
    }

    private void joinPlayer(Session session, JoinGameCommand action) throws IOException {
        ChessGame.TeamColor color = action.color().equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

        String stringOfColor = action.color().equalsIgnoreCase("white") ? "WHITE" : "BLACK";

        String expectedUsername = (color == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();

        if (!Objects.equals(expectedUsername, authData.username())) {
            sendError(session, new ErrorResult("Error: attempting to join with wrong color"));
            return;
        }

        var outgoingMessage = String.format("%s has joined %s team", authData.username(), stringOfColor);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, outgoingMessage);
        connections.broadcast(session, notification);
    }

    private void joinObserver(Session session, UserGameCommand action) {
        
    }

    private void exit(Session session, UserGameCommand action) throws IOException {
        var outgoingMessage = String.format("%s left the game", authData.username());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, outgoingMessage);
        connections.broadcast(session, notification);
    }

    private void makeMove(Session session, MakeMoveCommand action) {

    }

    private void resign (Session session, UserGameCommand action) {
    }

    private void sendError(Session session, ErrorResult error) throws IOException {
        System.out.printf("Error: %s%n", new Gson().toJson(error));
        session.getRemote().sendString(new Gson().toJson(error));
    }

    public void determineAction (Session session, UserGameCommand action) throws IOException {
        switch (action.getCommandType()) {
            case JOIN_OBSERVER -> joinObserver(session, action);
            case LEAVE -> exit(session, action);
            case RESIGN -> resign(session, action);
        }
    }

    public void setData (Session session, String authToken, int gameID) throws IOException {
        try {
            setAuthData(authToken);
            setGameData(gameID);
        } catch (Exception e) {
            sendError(session, new ErrorResult("Error: unauthorized"));
        }
    }

    public void setAuthData (String authToken) throws DataAccessException, ServiceException {
        authData = dataAccess.getAuth(authToken);
    }

    public void setGameData (int gameID) throws BadGameIDException, DataAccessException, ServiceException{
        gameData = dataAccess.getGame(gameID);
    }
}