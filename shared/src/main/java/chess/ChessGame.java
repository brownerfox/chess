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

    public ChessGame() {
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        if (this.team != null) {
            return this.team;
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
        return this.team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

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
        ChessBoard board = getBoard();
        ChessPiece piece = getBoard().getPiece(startPosition);

        Collection<ChessMove> currentMoves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : currentMoves) {
            if (testMove(move, board)) {
                validMoves.add(move);
            }
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

        if (piece == null || piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("No valid piece at start position or not the correct team's turn.");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("The move is not valid.");
        }

        if (move.getPromotionPiece() != null) {
            if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
                throw new InvalidMoveException("Piece can not be promoted.");
            } else {
                ChessPiece promotionPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                getBoard().addPiece(move.getEndPosition(), promotionPiece);
                getBoard().removePiece(move.getStartPosition().getRow(), move.getStartPosition().getColumn());
            }
        } else {

            getBoard().addPiece(move.getEndPosition(), piece);
            getBoard().removePiece(move.getStartPosition().getRow(), move.getStartPosition().getColumn());

            if (isInCheck(getTeamTurn())) {
                getBoard().addPiece(move.getStartPosition(), piece);
                getBoard().removePiece(move.getEndPosition().getRow(), move.getEndPosition().getColumn());
                throw new InvalidMoveException("This move places your king in check.");
            }
        }

        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    public boolean testMove(ChessMove move, ChessBoard chessBoard) {
        setTeamTurn(chessBoard.getPiece(move.getStartPosition()).getTeamColor());
        ChessPiece piece = chessBoard.getPiece(move.getStartPosition());
        ChessPiece copyPiece;

        if (piece == null || piece.getTeamColor() != getTeamTurn()) {
            return false;
        }

        Collection<ChessMove> validMoves = piece.pieceMoves(chessBoard, move.getStartPosition());

        if (!validMoves.contains(move)) {
            return false;
        }

        copyPiece = chessBoard.getPiece(move.getEndPosition());

        chessBoard.addPiece(move.getEndPosition(), piece);
        chessBoard.removePiece(move.getStartPosition().getRow(), move.getStartPosition().getColumn());

        if (isInCheck(getTeamTurn())) {
            if (copyPiece != null) {
                chessBoard.addPiece(move.getEndPosition(), copyPiece);
                chessBoard.addPiece(move.getStartPosition(), piece);
            } else {
                chessBoard.addPiece(move.getStartPosition(), piece);
                chessBoard.removePiece(move.getEndPosition().getRow(), move.getEndPosition().getColumn());
            }

            return false;
        }
        if (copyPiece != null) {
            chessBoard.addPiece(move.getEndPosition(), copyPiece);
            chessBoard.addPiece(move.getStartPosition(), piece);
        } else {
            chessBoard.addPiece(move.getStartPosition(), piece);
            chessBoard.removePiece((move.getEndPosition().getRow()), move.getEndPosition().getColumn());
        }

        return true;
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for (ChessPosition pos : getBoard().piecePositions()) {
            if (getBoard().getPiece(pos).getTeamColor() != teamColor) {
                if (getBoard().getPiece(pos).canCaptureKing(getBoard(), pos)) {
                    return true;
                }
            } // Find out how to use gather moves to see if we can shorten this function
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> teamMoves = new ArrayList<>();
        boolean inCheck = isInCheck(teamColor);

        if (!inCheck) {
            return false;
        }

        teamMoves.addAll(gatherTeamMoves(teamColor));

        for (ChessMove move : teamMoves) {
            if (testMove(move, getBoard())) {
                return false;
            }
        }

        return inCheck;
    }

        /**
         * Determines if the given team is in stalemate, which here is defined as having
         * no valid moves
         *
         * @param teamColor which team to check for stalemate
         * @return True if the specified team is in stalemate, otherwise false
         */
    public boolean isInStalemate (TeamColor teamColor){
        setTeamTurn(teamColor);
        Collection<ChessMove> teamMoves = new ArrayList<ChessMove>();

        teamMoves.addAll(gatherTeamMoves(teamColor));

        if (teamMoves.isEmpty()) {
            if (isInCheck(teamColor)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public Collection<ChessMove> gatherTeamMoves (TeamColor teamColor) {
        Collection<ChessMove> teamMoves = new ArrayList<>();

        for (ChessPosition pos : getBoard().piecePositions()) {
            ChessPiece piece = getBoard().getPiece(pos);
            if (piece.getTeamColor() == teamColor) {
                teamMoves.addAll(validMoves(pos));
            }
        }

        return teamMoves;

    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard (ChessBoard board){
        this.board = board;
        // Takes a given board and adds pieces to our board
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard () {
        if (this.board != null) {
            return this.board;
        } else {
            this.board = new ChessBoard();
            this.board.resetBoard();
        }
        return this.board;
    }
}

