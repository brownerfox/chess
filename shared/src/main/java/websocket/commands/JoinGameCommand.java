package websocket.commands;

public class JoinGameCommand extends UserGameCommand {
    UserGameCommand.CommandType commandType;
    String authToken;
    Integer gameID;
    String color;

    public JoinGameCommand(String authToken, Integer gameID, String color) {
        super(CommandType.JOIN_PLAYER, authToken, gameID);
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
}
