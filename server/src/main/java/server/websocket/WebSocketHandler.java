package server.websocket;

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

import javax.xml.crypto.Data;
import java.io.IOException;


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
            setData(session, action.getAuthToken(), action.getGameID());
            joinPlayer(action);
        } else if (message.contains("\"commandType\":\"MAKE_MOVE\"")) {
            MakeMoveCommand action = new Gson().fromJson(message, MakeMoveCommand.class);
            setData(session, action.getAuthToken(), action.getGameID());
            makeMove(action);
        } else {
            UserGameCommand action = new Gson().fromJson(message,UserGameCommand.class);
            setData(session, action.getAuthToken(), action.getGameID());
            determineAction(action);
        }
    }

    private void joinPlayer(JoinGameCommand action) throws IOException {
        var outgoingMessage = String.format("%s is in the shop", authData.username());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(notification);
    }

    private void joinObserver(UserGameCommand action) {
    }

    private void exit(UserGameCommand action) throws IOException {
        var outgoingMessage = String.format("%s left the shop", authData.username());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(notification);
    }

    private void makeMove(MakeMoveCommand action) {

    }

    private void resign (UserGameCommand action) {
    }

    private void sendError(Session session, ErrorResult error) throws IOException {
        System.out.printf("Error: %s%n", new Gson().toJson(error));
        session.getRemote().sendString(new Gson().toJson(error));
    }

    public void determineAction (UserGameCommand action) throws IOException {
        switch (action.getCommandType()) {
            case JOIN_OBSERVER -> joinObserver(action);
            case LEAVE -> exit(action);
            case RESIGN -> resign(action);
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