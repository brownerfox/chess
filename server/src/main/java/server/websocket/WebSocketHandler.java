package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.BadGameIDException;
import service.ServiceException;
import websocket.commands.UserGameCommand;
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

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {

        UserGameCommand action = new Gson().fromJson(message,UserGameCommand.class);
        setData(session, action.getAuthToken(), action.getGameID());
        if (action.getMove() == null && action.getColor() == null) {

            determineAction(session, action);
        } else if (action.getColor() != null) {
            if (gameData.whiteUsername() == null && gameData.blackUsername() == null) {
                joinObserver(session);
            } else if (gameData.whiteUsername() != null && gameData.blackUsername() != null) {
                sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Game is full!"));
            } else {
                joinPlayer(session, action);
            }
        } else {
            makeMove(session, action);
        }
    }

    private void joinPlayer(Session session, UserGameCommand action) throws IOException {
        connections.add(action.getGameID(), session);

        ChessGame.TeamColor color = action.getColor().equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

        String stringOfColor = action.getColor().equalsIgnoreCase("white") ? "WHITE" : "BLACK";

        String expectedUsername = (color == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();

        if (!Objects.equals(expectedUsername, authData.username())) {
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Error: attempting to join with wrong color!"));
            return;
        }

        var outgoingMessage = String.format("%s has joined the %s team!", authData.username(), stringOfColor);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage);
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,"");
        connections.broadcast(session, notification); //Sends the message that they joined to everyone
        connections.sendMessage(session, loadGameMessage); //Sends the board to the person who joined
    }

    private void joinObserver(Session session) throws IOException {
        connections.add(gameData.gameID(), session);

        var outgoingMessage = String.format("%s has joined as an observer!", authData.username());
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, outgoingMessage);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage);

        connections.broadcast(session, notification);
        connections.sendMessage(session, loadGameMessage);
    }

    private void leaveGame(Session session) throws IOException {
        var outgoingMessage = String.format("%s left the game!", authData.username());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage);
        connections.broadcast(session, notification);

        connections.remove(session);
    }

    private void makeMove(Session session, UserGameCommand action) throws IOException{
        ChessGame.TeamColor oppoColor = action.getColor().equalsIgnoreCase("white") ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        String oppoName = action.getColor().equalsIgnoreCase("white") ? gameData.blackUsername() : gameData.whiteUsername();
        if (gameData.game().getBoard().getPiece(action.getMove().getStartPosition()).getTeamColor() == oppoColor) {
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Error: This is not your piece"));
        }
        if (!Objects.equals(authData.username(), gameData.whiteUsername()) && !Objects.equals(authData.username(), gameData.blackUsername())){
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Error: You are only observing this game!"));
            return;
        }
        try {
            if (gameData.game().getTeamTurn().toString().equalsIgnoreCase(action.getColor())) {
                if (gameData.game().validMoves(action.getMove().getStartPosition()).contains(action.getMove())) {
                    gameData.game().makeMove(action.getMove());

                    ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,"");
                    connections.sendMessage(session, loadGameMessage);
                    connections.broadcast(session, loadGameMessage);

                    var outgoingMessage = String.format("%s has made a move!", authData.username());
                    ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage);
                    connections.broadcast(session, notification);
                }
            } else {
                sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"It is not your move!"));
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
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Not a valid move"));
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

    private void resign (Session session) throws IOException {
        ChessGame game = gameData.game();

        if (!Objects.equals(authData.username(), gameData.whiteUsername()) && !Objects.equals(authData.username(), gameData.blackUsername())) {
            sendError(session,new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Error: You are only observing this game!"));
            return;
        }

        if (game.getGameStatus()) {
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Error: Game is already finished"));
        }

        String outgoingMessage = String.format("%s has forfeited the game!", authData.username());
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage);
        connections.broadcast(session, serverMessage);
        game.setGameStatus(true);
        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        try {
            dataAccess.updateGame(newGameData);
        } catch (Exception e) {
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Unable to updata game"));
        }
    }

    private void sendError(Session session, ServerMessage error) throws IOException {
        session.getRemote().sendString(new Gson().toJson(error));
    }

    public void determineAction (Session session, UserGameCommand action) throws IOException {
        switch (action.getCommandType()) {
            case CONNECT -> joinObserver(session);
            case LEAVE -> leaveGame(session);
            case RESIGN -> resign(session);
        }
    }

    public void setData (Session session, String authToken, int gameID) throws IOException {
        try {
            setAuthData(authToken);
            setGameData(gameID);
        } catch (Exception e) {
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Error: unauthorized"));
        }
    }

    public void setAuthData (String authToken) throws DataAccessException, ServiceException {
        authData = dataAccess.getAuth(authToken);
    }

    public void setGameData (int gameID) throws BadGameIDException, DataAccessException, ServiceException{
        gameData = dataAccess.getGame(gameID);
    }
}