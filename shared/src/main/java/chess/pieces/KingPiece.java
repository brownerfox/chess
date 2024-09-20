package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingPiece extends ChessPiece{

    public KingPiece(ChessGame.TeamColor pieceColor, PieceType type, boolean hasMoved) {
        super(pieceColor, type, hasMoved);
    }

    public ChessGame.TeamColor getTeamColor() {
        return super.getTeamColor();
    }

    public PieceType getPieceType() {
        return super.getPieceType();
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */



    public Collection<ChessPosition> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessPosition> validChessMoves = new ArrayList<>();
        ChessPiece piece;

        if (myPosition.col != 8) {
            validChessMoves.add(new ChessPosition(myPosition.col + 1, myPosition.row));
        }
        if (myPosition.col != 0) {
            validChessMoves.add(new ChessPosition(myPosition.col - 1, myPosition.row));
        }
        if (myPosition.row != 8) {
            validChessMoves.add(new ChessPosition(myPosition.col, myPosition.row + 1));
        }
        if (myPosition.row != 0) {
            validChessMoves.add(new ChessPosition(myPosition.col, myPosition.row - 1));
        }


        return validChessMoves;

    }
}