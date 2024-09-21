package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoves implements PieceMoves {
    public ChessBoard board;
    public ChessPosition myPosition;

    public BishopMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validChessMoves = new ArrayList<>();
        int currCol, currRow;
        ChessPiece piece;

        int[][] directions = {
                {1, 1},   // up-right
                {-1, -1}, // down-left
                {-1, 1},  // up-left
                {1, -1}   // down-right
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
                    ChessPosition newPosition = ChessPosition.boardPosition(currRow, currCol);
                    piece = board.getPiece(ChessPosition.boardPosition(currRow, currCol));
                    if (piece != null && piece.pieceColor == board.getPiece(myPosition).pieceColor) {
                        canContinue = false;
                    } else if (piece != null) {
                        validChessMoves.add(new ChessMove(myPosition, newPosition, null));
                        canContinue = false;
                    } else {
                        validChessMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }

        return validChessMoves;
    }
}
