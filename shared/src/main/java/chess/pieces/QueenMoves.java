package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves implements PieceMoves {

    public ChessBoard board;
    public ChessPosition myPosition;

    public QueenMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validChessMoves = new ArrayList<>();
        int currCol, currRow;
        ChessPiece piece;

        // Define directions for diagonal movement (up-right, down-left, up-left, down-right)
        int[][] directions = {
                {1, 0},   // right
                {-1, 0}, // left
                {0, 1},  // up
                {0, -1},
                {1, 1},
                {-1, -1},
                {1, -1},
                {-1, 1}// down
        };

        // Iterate over each diagonal direction
        for (int[] direction : directions) {
            currCol = myPosition.getColumn();
            currRow = myPosition.getRow();
            boolean canContinue = true;

            // Move in the direction until you hit the edge of the board or a piece
            while (canContinue) {
                currCol += direction[0];
                currRow += direction[1];

                if (currCol < 0 || currCol >= 7 || currRow < 0 || currRow >= 7) {
                    canContinue = false;
                }

                if (currCol >= 0 && currCol < 8 && currRow >= 0 && currRow < 8) {
                    piece = board.getPiece(ChessPosition.boardPosition(currRow, currCol));
                    if (piece != null && piece.pieceColor == board.getPiece(myPosition).getTeamColor()) {
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


