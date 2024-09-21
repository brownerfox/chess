package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves implements PieceMoves {
    public ChessBoard board;
    public ChessPosition myPosition;

    public RookMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validChessMoves = new ArrayList<>();
        int currCol, currRow;
        ChessPiece piece;

        int[][] directions = {
                {1, 0},   // right
                {-1, 0}, // left
                {0, 1},  // up
                {0, -1}   // down
        };

        for (int[] direction : directions) {
            currCol = myPosition.getColumn();
            currRow = myPosition.getRow();
            boolean canContinue = true;

            while (canContinue) {
                currCol += direction[0];
                currRow += direction[1];

                if (currCol < 0 || currCol >= 7 || currRow < 0 || currRow >= 7) {
                    canContinue = false;
                }

                if (currCol >= 0 && currCol < 8 && currRow >= 0 && currRow < 8) {
                    piece = board.getPiece(new ChessPosition(currRow+1, currCol+1));
                    if (piece != null && piece.pieceColor == board.getPiece(myPosition).pieceColor) {
                        canContinue = false;
                    } else if (piece != null) {
                        ChessPosition newPosition = ChessPosition.boardPosition(currRow, currCol);
                        validChessMoves.add(new ChessMove(myPosition, newPosition, null));
                        canContinue = false;
                    } else {
                        ChessPosition newPosition = ChessPosition.boardPosition(currRow, currCol);
                        validChessMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }

        return validChessMoves;
    }
}

