package client.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import ui.BoardCreator;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.*;

public class WebSocketFacade {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    receiveMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void receiveMessage(String message) {
        if (message.contains("\"serverMessageType\":\"NOTIFICATION\"")) {
            ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
            printMessage(notification.getMessage());
        }
        else if (message.contains("\"serverMessageType\":\"ERROR\"")) {
            Error error = new Gson().fromJson(message, Error.class);
            printMessage(error.getMessage());
        }
        else if (message.contains("\"serverMessageType\":\"LOAD_GAME\"")) {
            LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
            printBoard(loadGame.getGame());
        }
    }

    public void printMessage(String message) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ERASE_LINE).append(message);
        System.out.print(stringBuilder);
    }

    public void printBoard(ChessGame game) {
        BoardCreator boardCreator = new BoardCreator(game);
        System.out.print(ERASE_LINE + "\n");
        boardCreator.printBoard();

    }
}
