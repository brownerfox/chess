package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Connection> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        var connection = new Connection(gameID, session);
        connections.put(gameID, connection);
    }

    public void remove(Session session) {
        connections.remove(visitorName);
    }

    public Connection findConnectionBySession(Session session) {
        return connections.get(session);
    }

    public void broadcast(String excludePlayerName, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.playerName.equals(excludePlayerName)) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.playerName);
        }
    }
}