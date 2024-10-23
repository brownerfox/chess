package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.KING;

public class MoveHelper {

    public MoveHelper() {
    }

    public boolean canCaptureKing(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        Collection<ChessMove> validMoves = piece.pieceMoves(board, position);

        for (ChessMove move : validMoves) {
            ChessPiece targetPiece = board.getPiece(move.getEndPosition());
            if (targetPiece != null && targetPiece.getPieceType() == KING &&
                    targetPiece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                return true;
            }
        }
        return false;
    }

    public Collection<ChessMove> findValidMoves(ChessBoard board, ChessPosition myPosition, int[][] moves) {
        return iteratePotentialMoves(board, myPosition, moves);
    }

    public Collection<ChessMove> iteratePotentialMoves (ChessBoard board, ChessPosition myPosition, int[][] moves) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece;
        ChessPosition position;

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
}
