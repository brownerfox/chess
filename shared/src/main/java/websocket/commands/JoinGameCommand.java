package websocket.commands;

import chess.ChessGame;

import javax.swing.*;

public class JoinGameCommand {
    private final UserGameCommand.CommandType commandType;
    private final String authToken;
    private final Integer gameID;
    private final ChessGame.TeamColor color;

    public JoinGameCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, ChessGame.TeamColor color) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.color = color;
    }

    public UserGameCommand.CommandType getCommandType() {
        return commandType;
    }
    public String getAuthToken() {
        return authToken;
    }
    public Integer getGameID() {
        return gameID;
    }
    public ChessGame.TeamColor getColor () {return color;}
}
