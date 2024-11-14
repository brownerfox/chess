package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import static ui.EscapeSequences.*;

public class BoardCreator {
    ChessGame game;
    ChessBoard board;

    public BoardCreator(ChessGame game) {
        this.game = game;
        board = game.getBoard();
    }

    public void updateGame(ChessGame game) {
        this.game = game;
    }

    public void printBoard(ChessGame.TeamColor teamColor) {
        StringBuilder output = new StringBuilder();
        if (teamColor == ChessGame.TeamColor.WHITE) {
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; i++) {
                    ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                    if (piece != null) {
                        output.append(pieceToString(piece));
                    } else {

                    }
                }
            }
        } else {
            for (int i = 8; i > 0; i--) {
                for (int j = 8; j > 0; j--) {
                    ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                }
            }
        }
    }

    private String pieceToString (ChessPiece piece) {
        StringBuilder output = new StringBuilder();

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            output.append(SET_TEXT_COLOR_GREEN);
            switch (piece.getPieceType()) {
                case QUEEN -> output.append(WHITE_QUEEN);
                case KING -> output.append(WHITE_KING);
                case BISHOP -> output.append(WHITE_BISHOP);
                case KNIGHT -> output.append(WHITE_KNIGHT);
                case ROOK -> output.append(WHITE_ROOK);
                case PAWN -> output.append(WHITE_PAWN);
            }
        } else {
            output.append(SET_TEXT_COLOR_MAGENTA);
            switch (piece.getPieceType()) {
                case QUEEN -> output.append(BLACK_QUEEN);
                case KING -> output.append(BLACK_KING);
                case BISHOP -> output.append(BLACK_BISHOP);
                case KNIGHT -> output.append(BLACK_KNIGHT);
                case ROOK -> output.append(BLACK_ROOK);
                case PAWN -> output.append(BLACK_PAWN);
            }
        }
            return output.toString();
        }


}
