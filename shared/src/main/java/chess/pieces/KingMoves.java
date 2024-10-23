package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class KingMoves implements PieceMoves {
    private ChessBoard board;
    private ChessPosition myPosition;

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
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece;
        ChessPosition position;
        int[][] moves = {{1, 1}, {-1, -1}, {1, -1}, {-1, 1}, {1, 0}, {-1, 0}, {0, -1}, {0, 1}};

        for (int[] move : moves) {
            int currRow = myPosition.getRow();
            int currCol = myPosition.getColumn();
            currRow += move[0];
            currCol += move[1];
            if (currRow < 0 || currRow >= 8 || currCol < 0 || currCol >= 8) {
                continue;
            } else {
                position = new ChessPosition(currRow + 1, currCol + 1);
                piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                    continue;
                } else {
                    validMoves.add(new ChessMove(myPosition, position, null));
                }
            }
        }

        return validMoves;
    }

    public boolean canCaptureKing(ChessBoard board, ChessPosition myPosition) {
        MoveHelper captureKing = new MoveHelper(board, myPosition);

        return captureKing.canCaptureKing();
    }
}