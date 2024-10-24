package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class KingMoves implements PieceMoves {
    private ChessBoard board;
    private ChessPosition myPosition;
    MoveHelper moveHelper = new MoveHelper();

    public KingMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        KingMoves kingMoves = (KingMoves) o;
        return Objects.equals(board, kingMoves.board) && Objects.equals(myPosition, kingMoves.myPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, myPosition);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] moves = {{1, 1}, {-1, -1}, {1, -1}, {-1, 1}, {1, 0}, {-1, 0}, {0, -1}, {0, 1}};
        return moveHelper.findValidSingleMoves(board, myPosition, moves);
    }

    public boolean canCaptureKing(ChessBoard board, ChessPosition myPosition) {
        return moveHelper.canCaptureKing(board, myPosition);
    }
}