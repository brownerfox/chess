package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightPiece extends ChessPiece{

    public KnightPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        super(pieceColor, type);
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
        int currCol, currRow;
        ChessPiece piece;

        // Define directions for diagonal movement (up-right, down-left, up-left, down-right)
        int[][] directions = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        // Iterate over each diagonal direction
        for (int[] direction : directions) {
            currCol = myPosition.col + direction[0];
            currRow = myPosition.row + direction[1];


            piece = board.getPiece(new ChessPosition(currRow, currCol));

            // If there's no piece or if there's an opponent's piece, it's a valid move
            if (piece == null || !piece.getTeamColor().equals(board.getPiece(myPosition).getTeamColor())) {
                validChessMoves.add(new ChessPosition(currRow, currCol));
            }

        }
        return validChessMoves;

    }
}


