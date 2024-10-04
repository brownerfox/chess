package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.KING;

public class RookMoves implements PieceMoves {
    private ChessBoard board;
    private ChessPosition myPosition;

    public RookMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int currRow, currCol;
        ChessPiece piece;
        ChessPosition position;
        int[][] moves = {{1, 0}, {-1,0}, {0, -1}, {0, 1}};

        for (int[] move: moves) {
            currRow = myPosition.getRow();
            currCol = myPosition.getColumn();
            boolean canContinue = true;

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