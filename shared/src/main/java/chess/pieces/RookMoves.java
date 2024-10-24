package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class RookMoves implements PieceMoves {
    private ChessBoard board;
    private ChessPosition myPosition;
    MoveHelper moveHelper = new MoveHelper();

    public RookMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        RookMoves rookMoves = (RookMoves) o;
        Boolean firstBoolean = Objects.equals(board, rookMoves.board);
        Boolean secondBoolean = Objects.equals(myPosition, rookMoves.myPosition);
        Boolean thirdBoolean = Objects.equals(moveHelper, rookMoves.moveHelper);
        return firstBoolean && secondBoolean && thirdBoolean;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, myPosition, moveHelper);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] moves = {{1, 0}, {-1,0}, {0, -1}, {0, 1}};
        return moveHelper.findValidConsecutiveMoves(board, myPosition, moves);
    }

    public boolean canCaptureKing(ChessBoard board, ChessPosition myPosition) {
        return moveHelper.canCaptureKing(board, myPosition);
    }
}