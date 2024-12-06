package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import results.ErrorResult;
import service.BadGameIDException;
import service.ServiceException;
import websocket.commands.JoinGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
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
        String query = session.getUpgradeRequest().getQueryString();
        if (query == null || !query.contains("authToken")) {
            System.out.println("Unauthorized connection attempt.");
            session.close(StatusCode.NORMAL, "Unauthorized");
            return;
        }
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
        connections.add(action.gameID(), session);

        ChessGame.TeamColor color = action.color().equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

        String stringOfColor = action.color().equalsIgnoreCase("white") ? "WHITE" : "BLACK";

        String expectedUsername = (color == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();

        if (!Objects.equals(expectedUsername, authData.username())) {
            sendError(session, new ErrorResult("Error: attempting to join with wrong color!"));
            return;
        }

        var outgoingMessage = String.format("%s has joined the %s team!", authData.username(), stringOfColor);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, outgoingMessage);
        LoadGameMessage loadGameMessage = new LoadGameMessage("", gameData.game());
        connections.broadcast(session, notification); //Sends the message that they joined to everyone
        connections.sendMessage(session, loadGameMessage); //Sends the board to the person who joined
    }

    private void joinObserver(Session session, UserGameCommand action) throws IOException {
        connections.add(gameData.gameID(), session);

        var outgoingMessage = String.format("%s has joined as an observer!", authData.username());
        LoadGameMessage loadGameMessage = new LoadGameMessage(outgoingMessage, gameData.game());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage);

        connections.broadcast(session, notification);
        connections.sendMessage(session, loadGameMessage);
    }

    private void leaveGame(Session session, UserGameCommand action) throws IOException {
        var outgoingMessage = String.format("%s left the game!", authData.username());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage);
        connections.broadcast(session, notification);

        session.close();
    }

    private void makeMove(Session session, MakeMoveCommand action) throws IOException{
        ChessGame.TeamColor oppoColor = action.color().equalsIgnoreCase("white") ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        String oppoName = action.color().equalsIgnoreCase("white") ? gameData.blackUsername() : gameData.whiteUsername();
        if (!Objects.equals(authData.username(), gameData.whiteUsername()) && !Objects.equals(authData.username(), gameData.blackUsername())){
            sendError(session, new ErrorResult("Error: You are only observing this game!"));
            return;
        }
        try {
            if (gameData.game().getTeamTurn().toString().equalsIgnoreCase(action.color())) {
                if (gameData.game().validMoves(action.move().getStartPosition()).contains(action.move())) {
                    gameData.game().makeMove(action.move());

                    LoadGameMessage loadGameMessage = new LoadGameMessage("", gameData.game());
                    connections.sendMessage(session, loadGameMessage);
                    connections.broadcast(session, loadGameMessage);

                    var outgoingMessage = String.format("%s has made a move!", authData.username());
                    ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage);
                    connections.broadcast(session, notification);
                }
            } else {
                sendError(session, new ErrorResult("It is not your move!"));
                return;
            }
            
            boolean checkMate = gameData.game().isInCheckmate(oppoColor);
            boolean staleMate = gameData.game().isInStalemate(oppoColor);
            boolean check = gameData.game().isInCheck(oppoColor);
            if (checkMate || staleMate || check) {
                ServerMessage notification = getStatusMessage(checkMate, staleMate, oppoName);
                connections.broadcast(session, notification);
                }

            dataAccess.updateGame(gameData);
        } catch (Exception e) {
            sendError(session, new ErrorResult("Not a valid move"));
        }
    }

    private ServerMessage getStatusMessage(boolean checkMate, boolean staleMate, String oppoName) {
        String outgoingMessage;
        if (checkMate) {
            outgoingMessage = String.format("The game has ended in checkmate in favor of %s!", authData.username());
        } else if (staleMate) {
            outgoingMessage = "The game has ended in stalemate!";
        } else {
            outgoingMessage = String.format("%s has put %s in check!", authData.username(), oppoName);
        }

        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage);
    }

    private void resign (Session session, UserGameCommand action) throws IOException {
        ChessGame game = gameData.game();

        if (!Objects.equals(authData.username(), gameData.whiteUsername()) && !Objects.equals(authData.username(), gameData.blackUsername())) {
            sendError(session, new ErrorResult("Error: You are only observing this game!"));
            return;
        }

        if (game.getGameStatus()) {
            sendError(session, new ErrorResult("Error: Game is already finished"));
        }

        String outgoingMessage = String.format("%s has forfeited the game!", authData.username());
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage);
        connections.broadcast(session, serverMessage);
        game.setGameStatus(true);
    }

    private void sendError(Session session, ErrorResult error) throws IOException {
        session.getRemote().sendString(new Gson().toJson(error));
    }

    public void determineAction (Session session, UserGameCommand action) throws IOException {
        switch (action.getCommandType()) {
            case JOIN_OBSERVER -> joinObserver(session, action);
            case LEAVE -> leaveGame(session, action);
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