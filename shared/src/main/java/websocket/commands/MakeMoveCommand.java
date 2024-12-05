package websocket.commands;

import chess.ChessMove;

public record MakeMoveCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, ChessMove move,
                              String color) {
}
