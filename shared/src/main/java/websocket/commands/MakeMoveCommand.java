package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

    UserGameCommand.CommandType commandType;
    String authToken;
    Integer gameID;
    ChessMove move;
    String color;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move, String color) {
        super(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
        this.color = color;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    public String getColor() {return color;}

    public ChessMove getMove() {return move;}

}
