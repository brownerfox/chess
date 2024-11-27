package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

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
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(action);
            case JOIN_OBSERVER -> joinObserver(action);
            case MAKE_MOVE -> makeMove(action);
            case LEAVE -> exit(action);
            case RESIGN -> resign(action);
        }
    }

    private void joinPlayer(UserGameCommand action) throws IOException {
        var outgoingMessage = String.format("%s is in the shop", playerName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(playerName, notification);
    }

    private void joinObserver(UserGameCommand action) {
    }

    private void exit(UserGameCommand action) throws IOException {
        connections.remove(visitorName);
        var outgoingMessage = String.format("%s left the shop", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(visitorName, notification);
    }

    private void makeMove(UserGameCommand action) {

    }

    private void resign (UserGameCommand action) {
    }
}