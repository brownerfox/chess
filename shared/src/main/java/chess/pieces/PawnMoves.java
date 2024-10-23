package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

public class PawnMoves implements PieceMoves {
    private ChessBoard board;
    private ChessPosition myPosition;

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
        Collection<ChessPiece.PieceType> potentialPromotions;
        potentialPromotions = new ArrayList<ChessPiece.PieceType>();
        ChessPiece piece;
        ChessPosition position;
        boolean canContinue = true;
        int[][] whiteForwardMoves = {{1, 0}, {2, 0}};
        int[][] whiteAttackingMoves = {{1, -1}, {1, 1}};
        int[][] blackForwardMoves = {{-1, 0}, {-2, 0}};
        int[][] blackAttackingMoves = {{-1, -1}, {-1, 1}};

        potentialPromotions.add(BISHOP);
        potentialPromotions.add(ROOK);
        potentialPromotions.add(KNIGHT);
        potentialPromotions.add(QUEEN);

        if (board.getPiece(myPosition).getTeamColor() == WHITE) {
            for (int[] move : whiteForwardMoves) {
                if (move[0] > 1 && myPosition.getRow() != 1) {
                    break;
                } else {
                    position = new ChessPosition(myPosition.getRow() + move[0] + 1, myPosition.getColumn() + move[1] + 1);
                    if (position.getRow() < 0 || position.getRow() >= 8 || position.getColumn() < 0 || position.getColumn() >= 8) {
                        break;
                    } else if (position.getRow() == 7) {
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
            for (int[] move : whiteAttackingMoves) {
                position = new ChessPosition(myPosition.getRow() + move[0]+1, myPosition.getColumn() + move[1]+1);
                if (position.getRow() < 0 || position.getRow() >= 8 || position.getColumn() < 0 || position.getColumn() >= 8) {
                    continue;
                } else if (position.getRow() == 7) {
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
        } else {
            for (int[] move : blackForwardMoves) {
                if (move[0] < -1 && myPosition.getRow() != 6) {
                    break;
                } else {
                    position = new ChessPosition(myPosition.getRow() + move[0] + 1, myPosition.getColumn() + move[1] + 1);
                    if (position.getRow() < 0 || position.getRow() >= 8 || position.getColumn() < 0 || position.getColumn() >= 8) {
                        break;
                    } else if (position.getRow() == 0) {
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
            for (int[] move : blackAttackingMoves) {
                position = new ChessPosition(myPosition.getRow() + move[0]+1 ,myPosition.getColumn() + move[1]+1);
                if (position.getRow() < 0 || position.getRow() >= 8 || position.getColumn() < 0 || position.getColumn() >= 8) {
                    continue;
                } else if (position.getRow() == 0) {
                    for (ChessPiece.PieceType promotion : potentialPromotions) {
                        piece = board.getPiece(position);
                        if (piece == null || piece.getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                            break;
                        } else {
                            validMoves.add(new ChessMove(myPosition, position, promotion));
                        }
                    }
                } else {
                    piece = board.getPiece(position);
                    if (piece == null) {
                        continue;
                    } else {
                        validMoves.add(new ChessMove(myPosition, position, null));
                    }
                }
            }
        }

        return validMoves;
    }

    public boolean canCaptureKing(ChessBoard board, ChessPosition myPosition) {
        MoveHelper captureKing = new MoveHelper();

        return captureKing.canCaptureKing(board, myPosition);
    }
}