package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoves implements PieceMoves {
    public ChessBoard board;
    public ChessPosition myPosition;

    public KingMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validChessMoves = new ArrayList<>();
        int currCol, currRow;
        ChessPiece piece;

        int[][] directions = {
                {1, 0},   // right
                {-1, 0}, // left
                {0, 1},  // up
                {0, -1},
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
                // down
        };

        for (int[] direction : directions) {
            currCol = myPosition.getColumn();
            currRow = myPosition.getRow();

            currCol += direction[0];
            currRow += direction[1];

            if (currCol < 0 || currCol >= 7 || currRow < 0 || currRow >= 7) {
                continue;
            } else {
                piece = board.getPiece(ChessPosition.boardPosition(currRow, currCol));
                if (piece != null && piece.pieceColor == board.getPiece(myPosition).pieceColor) {
                    continue;
                } else {
                    ChessPosition newPosition = ChessPosition.boardPosition(currRow, currCol);
                    validChessMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }




        return validChessMoves;

    }
}