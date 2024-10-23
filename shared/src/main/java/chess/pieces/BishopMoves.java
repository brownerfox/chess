package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class BishopMoves implements PieceMoves {
    private ChessBoard board;
    private ChessPosition myPosition;

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
        Collection<ChessMove> validMoves = new ArrayList<>();
        int currRow, currCol;
        ChessPiece piece;
        ChessPosition position;
        int[][] moves = {{1, 1}, {-1,-1}, {1, -1}, {-1, 1}};

        for (int[] move: moves) {
            boolean canContinue = true;
            currRow = myPosition.getRow();
            currCol = myPosition.getColumn();

            while (canContinue) {
                currRow += move[0];
                currCol += move[1];
                if (currRow < 0 || currRow >= 8 || currCol < 0 || currCol >= 8) {
                    canContinue = false;
                } else {
                    position = new ChessPosition(currRow+1, currCol+1);
                    piece = board.getPiece(position);
                    if (piece != null && piece.getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                        canContinue = false;
                    } else if (piece != null && piece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, position, null));
                        canContinue = false;
                    } else {
                        validMoves.add(new ChessMove(myPosition, position, null));
                    }
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
