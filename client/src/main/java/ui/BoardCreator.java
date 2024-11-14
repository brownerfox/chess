package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_BLACK;

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
            output.append(startAndEndRow(teamColor));
            for (int row = 8; row > 0; row--) {
                output.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_RED).append(String.format(" %d ", row));
                output.append(RESET_BG_COLOR);
                output.append(RESET_TEXT_COLOR);
                for (int col = 1; col < 9; col++) {
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    output.append(determineSquareColor(row, col));
                    if (piece != null) {
                        output.append(pieceToString(piece));
                    } else {
                        output.append("   ");
                    }
                }
                output.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_RED).append(String.format(" %d ", row));
                output.append(RESET_BG_COLOR);
                output.append(RESET_TEXT_COLOR);
            }
            output.append(startAndEndRow(teamColor));
        } else {
            output.append(startAndEndRow(teamColor));
            for (int row = 1; row < 9; row++) {
                output.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_RED).append(String.format(" %d ", row));
                output.append(RESET_BG_COLOR);
                output.append(RESET_TEXT_COLOR);
                for (int col = 8; col > 0; col--) {
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    output.append(determineSquareColor(row, col));
                    if (piece != null) {
                        output.append(pieceToString(piece));
                    } else {
                        output.append("   ");
                    }
                }
                output.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_RED).append(String.format(" %d ", row));
                output.append(RESET_BG_COLOR);
                output.append(RESET_TEXT_COLOR);
            }
            output.append(startAndEndRow(teamColor));
        }

        System.out.print(output.toString());
    }

    private String startAndEndRow (ChessGame.TeamColor teamColor) {
        StringBuilder output = new StringBuilder();
        if (teamColor == ChessGame.TeamColor.WHITE) {
            output.append(SET_BG_COLOR_LIGHT_GREY);
            output.append(SET_TEXT_COLOR_RED);
            output.append("    a  b  c  d  e  f  g  h    ");
            output.append(RESET_BG_COLOR);
            output.append(RESET_TEXT_COLOR);
            output.append("\n");
        } else {
            output.append(SET_BG_COLOR_LIGHT_GREY);
            output.append(SET_TEXT_COLOR_RED);
            output.append("    h  g  f  e  d  c  b  a    ");
            output.append(RESET_BG_COLOR);
            output.append(RESET_TEXT_COLOR);
            output.append("\n");
        }

        return output.toString();
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

    private String determineSquareColor(int row, int col) {
        StringBuilder output = new StringBuilder();
        if ((row + col) % 2 == 0) {
            output.append(SET_BG_COLOR_BLACK);
        } else {
            output.append(SET_BG_COLOR_WHITE);
        }
        return output.toString();
    }


}
