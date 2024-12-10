package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.put(session, gameID);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session session, ServerMessage notification) throws IOException {
        for (var c : connections.keySet()) {
            if (c.isOpen()) {
                if (c != session) {
                    if (Objects.equals(connections.get(c), connections.get(session))) {
                        sendMessage(c, notification);
                    }
                }
            }
        }
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }
}