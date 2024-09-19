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
        ChessPiece piece;
        Boolean isPiece = false;

        if (getTeamColor() == WHITE) {
            if (board.getPiece(new ChessPosition(myPosition.col, myPosition.row + 1)) == null) {
                validChessMoves.add(new ChessPosition(myPosition.col, myPosition.row + 1));
            }
            if (board.getPiece(new ChessPosition(myPosition.col, myPosition.row + 2)) == null && myPosition.row + 2 < 8 && !hasMoved) {
                validChessMoves.add(new ChessPosition(myPosition.col, myPosition.row + 2));
            }
            if (board.getPiece(new ChessPosition(myPosition.col - 1, myPosition.row + 1)) != null && myPosition.row + 1 < 8 && myPosition.col - 1 >= 0) {
                validChessMoves.add(new ChessPosition(myPosition.col - 1, myPosition.row + 1));
            }
            if (board.getPiece(new ChessPosition(myPosition.col + 1, myPosition.row + 1)) != null && myPosition.row + 1 < 8 && myPosition.col + 1 < 8) {
                validChessMoves.add(new ChessPosition(myPosition.col + 1, myPosition.row + 1));
            }
        } else {
            if (board.getPiece(new ChessPosition(myPosition.row, myPosition.col - 1)) == null) {
                validChessMoves.add(new ChessPosition(myPosition.row, myPosition.col - 1));
            }
            if (board.getPiece(new ChessPosition(myPosition.col, myPosition.row - 2)) == null && myPosition.row - 2 >= 0 && !hasMoved) {
                validChessMoves.add(new ChessPosition(myPosition.col, myPosition.row - 2));
            }
            if (board.getPiece(new ChessPosition(myPosition.col - 1, myPosition.row - 1)) != null && myPosition.row - 1 >= 0 && myPosition.col - 1 >= 0) {
                validChessMoves.add(new ChessPosition(myPosition.col - 1, myPosition.row - 1));
            }
            if (board.getPiece(new ChessPosition(myPosition.col + 1, myPosition.row - 1)) != null && myPosition.row - 1 >= 0 && myPosition.col + 1 < 8) {
                validChessMoves.add(new ChessPosition(myPosition.col + 1, myPosition.row - 1));
            }
        }


        return validChessMoves;
    }
}

