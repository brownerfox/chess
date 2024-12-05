package websocket.commands;

public record JoinGameCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, String color) {
}
