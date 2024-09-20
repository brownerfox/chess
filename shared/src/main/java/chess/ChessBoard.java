package chess;

import chess.pieces.*;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public ChessPiece[][] chessboard = new ChessPiece[8][8];

    public ChessBoard() {}


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.row;
        int col = position.col;


        chessboard[row][col] = piece;

    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (chessboard[position.row][position.col] != null) {
            return (chessboard[position.row][position.col]);
        } else {
            return null;
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        chessboard = new ChessPiece[8][8];

        chessboard[0][0] = new RookPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK, false);
        chessboard[0][1] = new KnightPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT, false);
        chessboard[0][2] = new BishopPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP, false);
        chessboard[0][4] = new KingPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING, false);
        chessboard[0][3] = new QueenPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN, false);
        chessboard[0][5] = new BishopPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP, false);
        chessboard[0][6] = new KnightPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT, false);
        chessboard[0][7] = new RookPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK, false);

        chessboard[1][0] = new PawnPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, false);
        chessboard[1][1] = new PawnPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, false);
        chessboard[1][2] = new PawnPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, false);
        chessboard[1][3] = new PawnPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, false);
        chessboard[1][4] = new PawnPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, false);
        chessboard[1][5] = new PawnPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, false);
        chessboard[1][6] = new PawnPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, false);
        chessboard[1][7] = new PawnPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN, false);

// Set black pieces on the bottom of the board (rows 6 and 7)
        chessboard[7][0] = new RookPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK, false);
        chessboard[7][1] = new KnightPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT, false);
        chessboard[7][2] = new BishopPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP, false);
        chessboard[7][3] = new QueenPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN, false);
        chessboard[7][4] = new KingPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING, false);
        chessboard[7][5] = new BishopPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP, false);
        chessboard[7][6] = new KnightPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT, false);
        chessboard[7][7] = new RookPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK, false);

        chessboard[6][0] = new PawnPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, false);
        chessboard[6][1] = new PawnPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, false);
        chessboard[6][2] = new PawnPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, false);
        chessboard[6][3] = new PawnPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, false);
        chessboard[6][4] = new PawnPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, false);
        chessboard[6][5] = new PawnPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, false);
        chessboard[6][6] = new PawnPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, false);
        chessboard[6][7] = new PawnPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN, false);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(chessboard, that.chessboard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessboard);
    }

    public String boardToString() {
        StringBuilder boardString = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            boardString.append("|");
            for (int j = 0; j < 8; j++) {
                if (chessboard[i][j] == null) {
                    boardString.append(" ");
                } else {
                    if (chessboard[i][j].getTeamColor() == ChessGame.TeamColor.WHITE) {
                        String pieceChar = switch (chessboard[i][j].getPieceType()) {
                            case BISHOP -> "B";
                            case ROOK -> "R";
                            case KNIGHT -> "N";
                            case QUEEN -> "Q";
                            case KING -> "K";
                            case PAWN -> "P";
                            default -> "?";  // Use "?" for an unrecognized piece
                            };
                        boardString.append(pieceChar);
                    } else {
                        String pieceChar = switch (chessboard[i][j].getPieceType()) {
                            case BISHOP -> "b";
                            case ROOK -> "r";
                            case KNIGHT -> "n";
                            case QUEEN -> "q";
                            case KING -> "k";
                            case PAWN -> "p";
                            default -> "?";  // Use "?" for an unrecognized piece
                        };
                        boardString.append(pieceChar);
                    };
                }
                boardString.append("|");
            }
            boardString.append("\n");
        }

        return boardString.toString();
    }

    public void printChessboard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.println(this.chessboard[i][j]);
            }
        }
    }
}
