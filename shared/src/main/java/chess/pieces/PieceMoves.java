package chess.pieces;

import chess.*;

import java.util.Collection;

public interface PieceMoves {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}
