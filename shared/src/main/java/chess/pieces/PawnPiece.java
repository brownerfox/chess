package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessGame.TeamColor.WHITE;

public class PawnPiece extends ChessPiece{

    public PawnPiece(ChessGame.TeamColor pieceColor, PieceType type, boolean hasMoved) {
        super(pieceColor, type, hasMoved);
    }

    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    public PieceType getPieceType() {
        return this.type;
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public Collection<ChessPosition> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessPosition> validChessMoves = new ArrayList<>();

        if (getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (myPosition.row + 1 < 8 && board.getPiece(new ChessPosition(myPosition.col, myPosition.row + 1)) == null) {
                validChessMoves.add(new ChessPosition(myPosition.col, myPosition.row + 1));
            }
            if (myPosition.row + 2 < 8 && board.getPiece(new ChessPosition(myPosition.col, myPosition.row + 2)) == null && !hasMoved) {
                validChessMoves.add(new ChessPosition(myPosition.col, myPosition.row + 2));
            }
            if (myPosition.row + 1 < 8 && myPosition.col - 1 >= 0) {
                ChessPiece leftDiagonalPiece = board.getPiece(new ChessPosition(myPosition.col - 1, myPosition.row + 1));
                if (leftDiagonalPiece != null && leftDiagonalPiece.getTeamColor() != getTeamColor()) {
                    validChessMoves.add(new ChessPosition(myPosition.col - 1, myPosition.row + 1));
                }
            }
            if (myPosition.row + 1 < 8 && myPosition.col + 1 < 8) {
                ChessPiece rightDiagonalPiece = board.getPiece(new ChessPosition(myPosition.col + 1, myPosition.row + 1));
                if (rightDiagonalPiece != null && rightDiagonalPiece.getTeamColor() != getTeamColor()) {
                    validChessMoves.add(new ChessPosition(myPosition.col + 1, myPosition.row + 1));
                }
            }
        } else {
            if (myPosition.row - 1 >= 0 && board.getPiece(new ChessPosition(myPosition.col, myPosition.row - 1)) == null) {
                validChessMoves.add(new ChessPosition(myPosition.col, myPosition.row - 1));
            }
            if (myPosition.row - 2 >= 0 && board.getPiece(new ChessPosition(myPosition.col, myPosition.row - 2)) == null && !hasMoved) {
                validChessMoves.add(new ChessPosition(myPosition.col, myPosition.row - 2));
            }
            if (myPosition.row - 1 >= 0 && myPosition.col - 1 >= 0) {
                ChessPiece leftDiagonalPiece = board.getPiece(new ChessPosition(myPosition.col - 1, myPosition.row - 1));
                if (leftDiagonalPiece != null && leftDiagonalPiece.getTeamColor() != getTeamColor()) {
                    validChessMoves.add(new ChessPosition(myPosition.col - 1, myPosition.row - 1));
                }
            }
            if (myPosition.row - 1 >= 0 && myPosition.col + 1 < 8) {
                ChessPiece rightDiagonalPiece = board.getPiece(new ChessPosition(myPosition.col + 1, myPosition.row - 1));
                if (rightDiagonalPiece != null && rightDiagonalPiece.getTeamColor() != getTeamColor()) {
                    validChessMoves.add(new ChessPosition(myPosition.col + 1, myPosition.row - 1));
                }
            }
        }





        return validChessMoves;
    }
}

