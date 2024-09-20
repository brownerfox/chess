package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RookPiece extends ChessPiece{

    public RookPiece(ChessGame.TeamColor pieceColor, PieceType type, boolean hasMoved) {
        super(pieceColor, type, hasMoved);
    }

    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    public PieceType getPieceType() {
        return this.type;
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public Collection<ChessPosition> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessPosition> validChessMoves = new ArrayList<>();
        int currCol, currRow;
        ChessPiece piece;

        int[][] directions = {
                {1, 0},   // right
                {-1, 0}, // left
                {0, 1},  // up
                {0, -1}   // down
        };

        for (int[] direction : directions) {
            currCol = myPosition.col;
            currRow = myPosition.row;
            boolean canContinue = true;

            while (canContinue) {
                currCol += direction[0];
                currRow += direction[1];

                if (currCol < 0 && currCol >= 8 && currRow < 0 && currRow >= 8) {
                    canContinue = false;
                }

                if (currCol >= 0 && currCol < 8 && currRow >= 0 && currRow < 8) {
                    piece = board.getPiece(new ChessPosition(currRow+1, currCol+1));
                    if (piece != null && piece.pieceColor == this.pieceColor) {
                        canContinue = false;
                    } else if (piece != null) {
                        validChessMoves.add(new ChessPosition(currRow, currCol));
                        canContinue = false;
                    } else {
                        validChessMoves.add(new ChessPosition(currRow, currCol));
                    }
                }
            }
        }

        return validChessMoves;
    }
}

