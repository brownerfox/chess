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
        ChessGame.TeamColor teamColor;

        UserGameCommand action = new Gson().fromJson(message,UserGameCommand.class);
        if (setData(session, action.getAuthToken(), action.getGameID())) {
            return;
        }
        if (action.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            resign(session);
        }
        if (action.getCommandType() == UserGameCommand.CommandType.LEAVE) {
            leaveGame(session);
        }
        if (action.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            if (!Objects.equals(authData.username(), gameData.blackUsername()) && !Objects.equals(authData.username(), gameData.whiteUsername())) {
                joinObserver(session);
            } else {
                if (Objects.equals(authData.username(), gameData.whiteUsername())) {
                    joinPlayer(session, action, ChessGame.TeamColor.WHITE);
                } else {
                    joinPlayer(session, action, ChessGame.TeamColor.BLACK);
                }
            }
        }

        if (action.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            if (Objects.equals(authData.username(), gameData.whiteUsername())) {
                makeMove(session, action, ChessGame.TeamColor.WHITE);
            } else {
                makeMove(session, action, ChessGame.TeamColor.BLACK);
            }
        }
    }

    private void joinPlayer(Session session, UserGameCommand action, ChessGame.TeamColor teamColor) throws IOException {
        connections.add(action.getGameID(), session);

        String stringOfColor = teamColor == ChessGame.TeamColor.WHITE ? "WHITE" : "BLACK";

        String expectedUsername = (teamColor == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();

        if (!Objects.equals(expectedUsername, authData.username())) {
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Error: attempting to join with wrong color!", null));
            return;
        }

        var outgoingMessage = String.format("%s has joined the %s team!", authData.username(), stringOfColor);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage, null);
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,null, gameData.game());
        connections.broadcast(session, notification); //Sends the message that they joined to everyone
        connections.sendMessage(session, loadGameMessage); //Sends the board to the person who joined
    }

    private void joinObserver(Session session) throws IOException {
        connections.add(gameData.gameID(), session);

        var outgoingMessage = String.format("%s has joined as an observer!", authData.username());
        ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, gameData.game());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage, null);

        connections.broadcast(session, notification);
        connections.sendMessage(session, loadGameMessage);
    }

    private void leaveGame(Session session) throws IOException {
        var outgoingMessage = String.format("%s left the game!", authData.username());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage, null);
        connections.broadcast(session, notification);
        try {
            if (Objects.equals(authData.username(), gameData.whiteUsername())) {
                GameData newGameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
                dataAccess.updateGame(newGameData);
                connections.remove(session);
            } else if (Objects.equals(authData.username(), gameData.blackUsername())) {
                GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
                dataAccess.updateGame(newGameData);
                connections.remove(session);
            } else {
                connections.remove(session);
            }
        } catch (Exception e) {
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Not a valid game", null));
        }
    }

    private void makeMove(Session session, UserGameCommand action, ChessGame.TeamColor teamColor) throws IOException{
        ChessGame.TeamColor oppoColor = teamColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        String oppoName = oppoColor == ChessGame.TeamColor.BLACK ? gameData.blackUsername() : gameData.whiteUsername();
        if (gameData.game().getGameStatus()) {
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: The game is already over", null));
            return;
        }
        if (gameData.game().getBoard().getPiece(action.getMove().getStartPosition()).getTeamColor() == oppoColor) {
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Error: This is not your piece", null));
            return;
        }
        if (!Objects.equals(authData.username(), gameData.whiteUsername()) && !Objects.equals(authData.username(), gameData.blackUsername())){
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Error: You are only observing this game!", null));
            return;
        }
        try {
            if (gameData.game().getTeamTurn().equals(teamColor)) {
                if (gameData.game().validMoves(action.getMove().getStartPosition()).contains(action.getMove())) {
                    gameData.game().makeMove(action.getMove());

                    ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,null, gameData.game());
                    connections.sendMessage(session, loadGameMessage);
                    connections.broadcast(session, loadGameMessage);

                } else {
                    sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Not a valid move", null));
                    return;
                }
            } else {
                sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"It is not your move!", null));
                return;
            }
            
            boolean checkMate = gameData.game().isInCheckmate(oppoColor);
            boolean staleMate = gameData.game().isInStalemate(oppoColor);
            boolean check = gameData.game().isInCheck(oppoColor);
            if (checkMate || staleMate || check) {
                ServerMessage notification = getStatusMessage(checkMate, staleMate, oppoName);
                connections.broadcast(session, notification);
                if (checkMate || staleMate) {
                    gameData.game().setGameStatus(true);
                }
                return;
            }

            var outgoingMessage = String.format("%s has made a move!", authData.username());
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage, null);
            connections.broadcast(session, notification);

            dataAccess.updateGame(gameData);
        } catch (Exception e) {
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Not a valid move", null));
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

        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage, null);
    }

    private void resign (Session session) throws IOException {
        ChessGame game = gameData.game();

        if (!Objects.equals(authData.username(), gameData.whiteUsername()) && !Objects.equals(authData.username(), gameData.blackUsername())) {
            sendError(session,new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Error: You are only observing this game!", null));
            return;
        }

        if (game.getGameStatus()) {
            System.out.print(game.getGameStatus());
            System.out.print("It gets flagged for the game being completed");
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR,"Error: Game is already finished", null));
            return;
        }
        String outgoingMessage = String.format("%s has forfeited the game!", authData.username());
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, outgoingMessage, null);
        connections.broadcast(session, serverMessage);
        connections.sendMessage(session, serverMessage);
        game.setGameStatus(true);
        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        try {
            dataAccess.updateGame(newGameData);
        } catch (Exception e) {
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Unable to updata game", null));
        }
    }

    private void sendError(Session session, ServerMessage error) throws IOException {
        session.getRemote().sendString(new Gson().toJson(error));
    }

    public boolean setData (Session session, String authToken, int gameID) throws IOException {
        try {
            setAuthData(authToken);
            setGameData(gameID);
            return false;
        } catch (Exception e) {
            sendError(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: unauthorized", null));
            return true;
        }
    }

    public void setAuthData (String authToken) throws DataAccessException, ServiceException {
        authData = dataAccess.getAuth(authToken);
    }

    public void setGameData (int gameID) throws BadGameIDException, DataAccessException, ServiceException{
        gameData = dataAccess.getGame(gameID);
    }
}