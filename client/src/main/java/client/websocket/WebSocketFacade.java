package client.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import ui.BoardCreator;
import websocket.commands.JoinGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.*;

public class WebSocketFacade {

    private final Session session;
    private ChessGame.TeamColor teamColor;

    public WebSocketFacade(String url, ChessGame.TeamColor teamColor) throws ResponseException {
        this.teamColor = teamColor;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

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
        System.out.print(ERASE_LINE + message);
    }

    public void printBoard(ChessGame game) {
        BoardCreator boardCreator = new BoardCreator(game, teamColor);
        System.out.print(ERASE_LINE + "\n");
        boardCreator.printBoard();

    }

    public void sendMessage(UserGameCommand command) {
        String message = new Gson().toJson(command);
        this.session.getAsyncRemote().sendText(message);
    }

    public void joinPlayer(JoinGameCommand command) {
        sendMessage(command);
    }

    public void joinObserver(UserGameCommand command) {
        sendMessage(command);
    }

    public void makeMove(MakeMoveCommand command) {
        sendMessage(command);
    }

    public void leave(UserGameCommand command) {
        sendMessage(command);
    }

    public void resign(UserGameCommand command) {
        sendMessage(command);
    }

}
