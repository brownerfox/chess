package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopPiece extends ChessPiece{

    public BishopPiece(ChessGame.TeamColor pieceColor, PieceType type, boolean hasMoved) {
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
                {1, 1},   // up-right
                {-1, -1}, // down-left
                {-1, 1},  // up-left
                {1, -1}   // down-right
        };

        for (int[] direction : directions) {
            currCol = myPosition.col;
            currRow = myPosition.row;
            boolean isPiece = true;

            while (currCol >= 0 && currCol < 8 && currRow >= 0 && currRow < 8 && isPiece) {
                currCol += direction[0];
                currRow += direction[1];

                if (currCol >= 0 && currCol < 8 && currRow >= 0 && currRow < 8) {
                    piece = board.getPiece(new ChessPosition(currRow, currCol));
                    if (piece != null) {
                        validChessMoves.add(new ChessPosition(currRow, currCol));
                        isPiece = false;
                    } else {
                        validChessMoves.add(new ChessPosition(currRow, currCol));
                    }
                }
            }
        }

        return validChessMoves;
    }
}