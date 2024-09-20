package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightPiece extends ChessPiece{

    public KnightPiece(ChessGame.TeamColor pieceColor, PieceType type, boolean hasMoved) {
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
        Collection<ChessPosition> validChessMoves = new ArrayList<>();
        int currCol, currRow;
        ChessPiece piece;


        int[][] directions = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] direction : directions) {
            currCol = myPosition.col + direction[0];
            currRow = myPosition.row + direction[1];

            if ((currCol < 0 || currCol >= 8 || currRow < 0 || currRow >= 8)) {
                continue;
            }


            piece = board.getPiece(new ChessPosition(currRow, currCol));

            if (piece == null || !piece.getTeamColor().equals(board.getPiece(myPosition).getTeamColor())) {
                validChessMoves.add(new ChessPosition(currRow, currCol));
            }

        }
        return validChessMoves;

    }
}


