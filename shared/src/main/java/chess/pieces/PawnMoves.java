package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

public class PawnMoves implements PieceMoves {
    private ChessBoard board;
    private ChessPosition myPosition;
    MoveHelper moveHelper = new MoveHelper();

    public PawnMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        PawnMoves pawnMoves = (PawnMoves) o;
        return Objects.equals(board, pawnMoves.board) && Objects.equals(myPosition, pawnMoves.myPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, myPosition);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        int[][] whiteForwardMoves = {{1, 0}, {2, 0}};
        int[][] whiteAttackingMoves = {{1, -1}, {1, 1}};
        int[][] blackForwardMoves = {{-1, 0}, {-2, 0}};
        int[][] blackAttackingMoves = {{-1, -1}, {-1, 1}};

        if (board.getPiece(myPosition).getTeamColor() == WHITE) {
            validMoves.addAll(moveHelper.findPawnForwardMoves(board, myPosition, whiteForwardMoves, 1, 7));
            validMoves.addAll(moveHelper.findPawnAttackingMoves(board, myPosition, whiteAttackingMoves, 7));
        } else {
            validMoves.addAll(moveHelper.findPawnForwardMoves(board, myPosition, blackForwardMoves, 6, 0));
            validMoves.addAll(moveHelper.findPawnAttackingMoves(board, myPosition, blackAttackingMoves, 0));
        }

        return validMoves;
    }

    public boolean canCaptureKing(ChessBoard board, ChessPosition myPosition) {
        return moveHelper.canCaptureKing(board, myPosition);
    }
}