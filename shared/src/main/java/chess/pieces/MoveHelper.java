package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static chess.ChessPiece.PieceType.*;
import static java.lang.Math.abs;

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

    public Collection<ChessMove> findValidConsecutiveMoves (ChessBoard board, ChessPosition myPosition, int[][] moves) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int currRow, currCol;
        ChessPiece piece;
        ChessPosition position;

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

    public Collection<ChessMove> findValidSingleMoves (ChessBoard board, ChessPosition myPosition, int[][] moves) {
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

    public Collection<ChessMove> findPawnForwardMoves (ChessBoard board, ChessPosition myPosition, int[][] moves, int initialRow, int promotionRow) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessPiece.PieceType> potentialPromotions= new ArrayList<ChessPiece.PieceType>(Arrays.asList(BISHOP, ROOK, KNIGHT, QUEEN));
        ChessPiece piece;
        ChessPosition position;

        for (int[] move : moves) {
            if (abs(move[0]) > 1 && myPosition.getRow() != initialRow) {
                break;
            } else {
                position = new ChessPosition(myPosition.getRow() + move[0] + 1, myPosition.getColumn() + move[1] + 1);
                if (position.getRow() < 0 || position.getRow() >= 8 || position.getColumn() < 0 || position.getColumn() >= 8) {
                    break;
                } else if (position.getRow() == promotionRow) {
                    for (ChessPiece.PieceType promotion : potentialPromotions) {
                        piece = board.getPiece(position);
                        if (piece != null) {
                            break;
                        } else {
                            validMoves.add(new ChessMove(myPosition, position, promotion));
                        }
                    }
                } else {
                    piece = board.getPiece(position);
                    if (piece != null) {
                        break;
                    } else {
                        validMoves.add(new ChessMove(myPosition, position, null));
                    }
                }
            }
        }

        return validMoves;
    }

    public Collection<ChessMove> findPawnAttackingMoves(ChessBoard board, ChessPosition myPosition, int[][] moves, int promotionRow) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessPiece.PieceType> potentialPromotions= new ArrayList<ChessPiece.PieceType>(Arrays.asList(BISHOP, ROOK, KNIGHT, QUEEN));
        ChessPiece piece;
        ChessPosition position;

        for (int[] move : moves) {
            position = new ChessPosition(myPosition.getRow() + move[0]+1, myPosition.getColumn() + move[1]+1);
            if (position.getRow() < 0 || position.getRow() >= 8 || position.getColumn() < 0 || position.getColumn() >= 8) {
                continue;
            } else if (position.getRow() == promotionRow) {
                for (ChessPiece.PieceType promotion : potentialPromotions) {
                    piece = board.getPiece(position);
                    if (piece == null) {
                        break;
                    } else {
                        validMoves.add(new ChessMove(myPosition, position, promotion));
                    }
                }
            } else {
                piece = board.getPiece(position);
                if (piece == null || piece.getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                    continue;
                } else {
                    validMoves.add(new ChessMove(myPosition, position, null));
                }
            }
        }
        return validMoves;
    }
}
