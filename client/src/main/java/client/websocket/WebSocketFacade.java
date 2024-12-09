package client.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import ui.BoardCreator;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class WebSocketFacade extends Endpoint {

    private final Session session;
    private ChessGame.TeamColor teamColor;
    private int gameID;
    private String authToken;
    private ChessGame game;

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
           ServerMessage error = new Gson().fromJson(message, ServerMessage.class);
            printMessage(error.getMessage());
        }
        else if (message.contains("\"serverMessageType\":\"LOAD_GAME\"")) {
            ServerMessage loadGame = new Gson().fromJson(message, ServerMessage.class);
            game = loadGame.getGame();
            printBoard();
        }
    }

    public void printMessage(String message) {
        System.out.print(ERASE_LINE + message);
    }

    public void printBoard() {
        BoardCreator boardCreator = new BoardCreator(game, teamColor);
        System.out.print(ERASE_LINE + "\n");
        boardCreator.printBoard(null);
    }

    public void printHighlightedBoard(ChessPosition position) {
        Collection<ChessMove> moves = game.validMoves(position);
        BoardCreator boardCreator = new BoardCreator(game, teamColor);
        boardCreator.printBoard(moves);
    }

    public void sendMessage(UserGameCommand command) {
        String message = new Gson().toJson(command);
        this.session.getAsyncRemote().sendText(message);
    }

    public void joinPlayer(UserGameCommand command) {
        setAuthToken(command.getAuthToken());
        setGameID(command.getGameID());
        sendMessage(command);
    }

    public void joinObserver(UserGameCommand command) {
        setAuthToken(command.getAuthToken());
        setGameID(command.getGameID());
        sendMessage(command);
    }

    public void makeMove(ChessMove move) {
        sendMessage(new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move, teamColor.toString()));
    }

    public void leave() {
        sendMessage(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, null, null));
    }

    public void resign() {
        sendMessage(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID, null, null));
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
