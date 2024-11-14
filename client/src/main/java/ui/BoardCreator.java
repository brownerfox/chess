package ui;

import chess.ChessGame;

public class BoardCreator {
    ChessGame game;

    public BoardCreator(ChessGame game) {
        this.game = game;
    }

    public void updateGame(ChessGame game) {
        this.game = game;
    }

    public void printBoard(ChessGame.TeamColor teamColor) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            
        }
    }
}
