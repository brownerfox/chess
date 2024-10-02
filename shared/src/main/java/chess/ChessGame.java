package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor team;
    private ChessBoard board;

    public ChessGame() {}

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {this.team = team;}

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessBoard copyBoard = getBoard();
        ChessPiece piece = getBoard().getPiece(startPosition);


        if (isInCheck(getTeamTurn())) {
            if (isInCheckmate(getTeamTurn())) {
                // end the game
            } else {
                Collection<ChessMove> currentMoves = piece.pieceMoves(getBoard(), startPosition);
                for (ChessMove move : currentMoves) {
                    makeMove(move);
                }
                // iterate over moves of the given piece to see if it ends check
            }
        } else if (isInStalemate(getTeamTurn())) {
                // end the game
        } else {
                // iterate over moves to see if the put the king in check
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = getBoard().getPiece(move.getStartPosition());

        getBoard().addPiece(move.getEndPosition(), piece);
        getBoard().removePiece(move.getStartPosition().getRow(), move.getStartPosition().getColumn());
        // If move is not in validMoves then throw an invalid move exception



    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for (ChessPosition pos : board.piecePositions()) {
            if (board.getPiece(pos).getTeamColor() != teamColor) {
                if (board.getPiece(pos).pieceMoves(board, pos).)
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // After checking for check, see if our king or any pieces can move to protect our king or take opposing pieces
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // If piece is not in check, check to see if king or any other pieces can move
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        // Takes a given board and adds pieces to our board
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        ChessBoard chessboard = new ChessBoard();

        return chessboard;
    }
}
