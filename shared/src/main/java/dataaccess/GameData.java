package dataaccess;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    GameData addBlackPlayer (String username) {
        return new GameData(gameID, whiteUsername, username, gameName, game);
    }

    GameData addWhitePlayer (String username) {
        return new GameData(gameID, username, blackUsername, gameName, game);
    }

    GameData updateGame (ChessGame newGame) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, newGame);
    }
}
