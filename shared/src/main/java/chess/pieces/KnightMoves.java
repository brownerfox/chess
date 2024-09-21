package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves implements PieceMoves {

    public ChessBoard board;
    public ChessPosition myPosition;

    public KnightMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validChessMoves = new ArrayList<>();
        int currCol, currRow;
        ChessPiece piece;


        int[][] directions = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] direction : directions) {
            currCol = myPosition.getColumn() + direction[0];
            currRow = myPosition.getRow() + direction[1];
            ChessPosition newPosition = ChessPosition.boardPosition(currRow, currCol);

            if ((currCol < 0 || currCol >= 8 || currRow < 0 || currRow >= 8)) {
                continue;
            }


            piece = board.getPiece(ChessPosition.boardPosition(currRow, currCol));

            if (piece == null || !piece.getTeamColor().equals(board.getPiece(myPosition).getTeamColor())) {
                validChessMoves.add(new ChessMove(myPosition, newPosition, null));
            }

        }
        return validChessMoves;

    }
}


