package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;

    private final ChessMove move;

    private final String color;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move, String color) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = move;
        this.color = color;
    }

    public enum CommandType {
        MAKE_MOVE,
        LEAVE,
        CONNECT,
        RESIGN
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand)) {
            return false;
        }
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
