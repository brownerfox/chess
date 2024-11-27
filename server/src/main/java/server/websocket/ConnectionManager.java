package server.websocket;

import com.mysql.cj.exceptions.ConnectionIsClosedException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.put(session, gameID);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Session>();
        for (var c : connections.keySet()) {
            if (c.isOpen()) {
                Connection connection = new Connection(connections.get(c), c);
                connection.send(notification.toString());
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c);
        }
    }
}