package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessPiece.PieceType.KING;

public class KnightMoves implements PieceMoves {
    private ChessBoard board;
    private ChessPosition myPosition;

    public KnightMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnightMoves that = (KnightMoves) o;
        return Objects.equals(board, that.board) && Objects.equals(myPosition, that.myPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, myPosition);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece;
        ChessPosition position;
        boolean canContinue = true;
        int[][] moves = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

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
        Collection<ChessMove> validMoves = pieceMoves(this.board, this.myPosition);

        for (ChessMove move : validMoves) {
            ChessPiece targetPiece = board.getPiece(move.getEndPosition());
            if (targetPiece != null && targetPiece.getPieceType() == KING &&
                    targetPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                return true;
            }
        }
        return false;
    }
}