package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import results.ErrorResult;
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

        try {
            UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
            authData = dataAccess.getAuth(action.getAuthToken());
            gameData = dataAccess.getGame(action.getGameID());
            switch (action.getCommandType()) {
                case JOIN_PLAYER -> joinPlayer(action);
                case JOIN_OBSERVER -> joinObserver(action);
                case MAKE_MOVE -> makeMove(action);
                case LEAVE -> exit(action);
                case RESIGN -> resign(action);
            }
        } catch (Exception e) {
            sendError(session, new ErrorResult("Error: unauthorized"));
        }
    }

    private void joinPlayer(UserGameCommand action) throws IOException {
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

    private void makeMove(UserGameCommand action) {

    }

    private void resign (UserGameCommand action) {
    }

    private void sendError(Session session, ErrorResult error) throws IOException {
        System.out.printf("Error: %s%n", new Gson().toJson(error));
        session.getRemote().sendString(new Gson().toJson(error));
    }
}