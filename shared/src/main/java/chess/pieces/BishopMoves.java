package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class BishopMoves implements PieceMoves {
    private ChessBoard board;
    private ChessPosition myPosition;
    MoveHelper moveHelper = new MoveHelper();

    public BishopMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        BishopMoves that = (BishopMoves) o;
        return Objects.equals(board, that.board) && Objects.equals(myPosition, that.myPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, myPosition);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] moves = {{1, 1}, {-1,-1}, {1, -1}, {-1, 1}};
        return moveHelper.findValidConsecutiveMoves(board, myPosition, moves);
    }

    public boolean canCaptureKing(ChessBoard board, ChessPosition myPosition) {
        return moveHelper.canCaptureKing(board, myPosition);
    }
}
