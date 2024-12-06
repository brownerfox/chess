package ui;

import chess.*;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_BLACK;

public class BoardCreator {
    ChessGame game;
    ChessBoard board;
    private final ChessGame.TeamColor teamColor;

    public BoardCreator(ChessGame game, ChessGame.TeamColor teamColor) {
        this.game = game;
        this.teamColor = teamColor;
        board = game.getBoard();
    }

    public void printBoard(Collection<ChessMove> validMoves) {
        StringBuilder output = new StringBuilder();
        output.append(startAndEndRow(teamColor));

        int rowStart = (teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int rowEnd = (teamColor == ChessGame.TeamColor.WHITE) ? 0 : 9;
        int rowStep = (teamColor == ChessGame.TeamColor.WHITE) ? -1 : 1;

        for (int row = rowStart; row != rowEnd; row += rowStep) {
            output.append(SET_BG_COLOR_DARK_GREY).append(SET_TEXT_COLOR_RED)
                    .append(String.format(" %d ", row)).append(RESET_BG_COLOR).append(RESET_TEXT_COLOR);

            int colStart = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : 8;
            int colEnd = (teamColor == ChessGame.TeamColor.WHITE) ? 9 : 0;
            int colStep = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

            for (int col = colStart; col != colEnd; col += colStep) {
                if (validMoves == null) {
                    output.append(determineSquareColor(row, col));
                } else {
                    output.append(determineSquareColor(row, col, validMoves));
                }
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null) {
                    output.append(pieceToString(piece));
                } else {
                    output.append("   ");
                }
                output.append(RESET_BG_COLOR).append(RESET_TEXT_COLOR);
            }

            output.append(SET_BG_COLOR_DARK_GREY).append(SET_TEXT_COLOR_RED)
                    .append(String.format(" %d ", row)).append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append("\n");
        }

        output.append(startAndEndRow(teamColor));
        System.out.print(output);
    }


    private String startAndEndRow (ChessGame.TeamColor teamColor) {
        StringBuilder output = new StringBuilder();
        if (teamColor == ChessGame.TeamColor.WHITE) {
            output.append(SET_BG_COLOR_DARK_GREY);
            output.append(SET_TEXT_COLOR_RED);
            output.append("    a  b  c  d  e  f  g  h    ");
            output.append(RESET_BG_COLOR);
            output.append(RESET_TEXT_COLOR);
            output.append("\n");
        } else {
            output.append(SET_BG_COLOR_DARK_GREY);
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
            output.append(SET_TEXT_COLOR_RED);
        } else {
            output.append(SET_TEXT_COLOR_BLUE);
        }
        output.append(SET_TEXT_BOLD);
        switch (piece.getPieceType()) {
            case QUEEN -> output.append(" Q ");
            case KING -> output.append(" K ");
            case BISHOP -> output.append(" B ");
            case KNIGHT -> output.append(" k ");
            case ROOK -> output.append(" R ");
            case PAWN -> output.append(" P ");
        }
        output.append(RESET_TEXT_BOLD_FAINT);
        return output.toString();
    }

    private String determineSquareColor(int row, int col) {
        return determineSquareColor(row, col, null);
    }

    private String determineSquareColor(int row, int col, Collection<ChessMove> validMoves) {
        Set<ChessPosition> validPositions;
        validPositions = validMoves.stream()
                .map(ChessMove::getEndPosition)
                .collect(Collectors.toSet());

        ChessPosition position = new ChessPosition(row, col);

        StringBuilder output = new StringBuilder();
        if ((row + col) % 2 == 0) {
            if (validPositions.contains(position)) {
                output.append(SET_BG_COLOR_DARK_GREEN);
            } else {
                output.append(SET_BG_COLOR_BLACK);
            }
        } else {
            if (validPositions.contains(position)) {
                output.append(SET_BG_COLOR_GREEN);
            } else {
                output.append(SET_BG_COLOR_LIGHT_GREY);
            }
        }
        return output.toString();
    }


}
