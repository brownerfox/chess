package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.*;

public class PawnMoves implements PieceMoves {
    public ChessBoard board;
    public ChessPosition myPosition;

    public PawnMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validChessMoves = new ArrayList<>();

        Collection<ChessPiece.PieceType> potentialPromotions;
        potentialPromotions = new ArrayList<ChessPiece.PieceType>();
        potentialPromotions.add(BISHOP);
        potentialPromotions.add(ROOK);
        potentialPromotions.add(KNIGHT);
        potentialPromotions.add(QUEEN);

        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (myPosition.getRow() + 1 < 8 && board.getPiece(myPosition.translate(1, 0)) == null) {
                ChessPosition newPosition = myPosition.translate(1, 0);
                if (newPosition.getRow() == 7) {
                    for (ChessPiece.PieceType promotion : potentialPromotions) {
                        validChessMoves.add(new ChessMove(myPosition, newPosition, promotion));
                    }
                } else {
                    validChessMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
            if (myPosition.getRow() + 2 < 8 && board.getPiece(myPosition.translate(2, 0)) == null && myPosition.getRow() == 1) {
                ChessPosition newPosition = myPosition.translate(2, 0);
                validChessMoves.add(new ChessMove(myPosition, newPosition, null));
            }
            if (myPosition.getRow() + 1 < 8 && myPosition.getColumn() - 1 >= 0) {
                ChessPiece leftDiagonalPiece = board.getPiece(myPosition.translate(1, -1));
                if (leftDiagonalPiece != null && leftDiagonalPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    ChessPosition newPosition = myPosition.translate(1, -1);
                    if (newPosition.getRow() == 7) {
                        for (ChessPiece.PieceType promotion : potentialPromotions) {
                            validChessMoves.add(new ChessMove(myPosition, newPosition, promotion));
                        }
                    } else {
                        validChessMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
            if (myPosition.getRow() + 1 < 8 && myPosition.getColumn() + 1 < 8) {
                ChessPiece rightDiagonalPiece = board.getPiece(myPosition.translate(1, 1));
                if (rightDiagonalPiece != null && rightDiagonalPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    ChessPosition newPosition = myPosition.translate(1, 1);
                    if (newPosition.getRow() == 7) {
                        for (ChessPiece.PieceType promotion : potentialPromotions) {
                            validChessMoves.add(new ChessMove(myPosition, newPosition, promotion));
                        }
                    } else {
                        validChessMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }
            else { // Black
                if (myPosition.getRow() - 1 >= 0 && board.getPiece(myPosition.translate(-1, 0)) == null) {
                    ChessPosition newPosition = myPosition.translate(-1, 0);
                    if (newPosition.getRow() == 0) {
                        for (ChessPiece.PieceType promotion : potentialPromotions) {
                            validChessMoves.add(new ChessMove(myPosition, newPosition, promotion));
                        }
                    } else {
                        validChessMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
                if (myPosition.getRow() - 2 >= 0 && board.getPiece(myPosition.translate(-1, 0)) == null && board.getPiece(myPosition.translate(-2, 0)) == null && myPosition.getRow() == 6) {
                    ChessPosition newPosition = myPosition.translate(-2, 0);
                    validChessMoves.add(new ChessMove(myPosition, newPosition, null));
                }

                if (myPosition.getRow() - 1 >= 0 && myPosition.getColumn() - 1 >= 0) {
                    ChessPiece leftDiagonalPiece = board.getPiece(myPosition.translate(-1, -1));
                    if (leftDiagonalPiece != null && leftDiagonalPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessPosition newPosition = myPosition.translate(-1, -1);
                        if (newPosition.getRow() == 0) {
                            for (ChessPiece.PieceType promotion : potentialPromotions) {
                                validChessMoves.add(new ChessMove(myPosition, newPosition, promotion));
                            }
                        } else {
                            validChessMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }

                    if (myPosition.getRow() - 1 >= 0 && myPosition.getColumn() + 1 < 8) {
                        ChessPiece rightDiagonalPiece = board.getPiece(myPosition.translate(-1, 1));
                        if (rightDiagonalPiece != null && rightDiagonalPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                            ChessPosition newPosition = myPosition.translate(-1, 1);
                            if (newPosition.getRow() == 0) {
                                for (ChessPiece.PieceType promotion : potentialPromotions) {
                                    validChessMoves.add(new ChessMove(myPosition, newPosition, promotion));
                                }
                            } else {
                                validChessMoves.add(new ChessMove(myPosition, newPosition, null));
                            }
                        }
                    }
                }
            }
            return validChessMoves;
        }
    }