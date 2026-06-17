package ph.edu.dlsu.lbycpob.chessapp.model.pieces;

import ph.edu.dlsu.lbycpob.chessapp.model.ChessBoard;

/**
 * Represents a King chess piece, the most important piece on the board.
 * The King can move one square in any direction (horizontally, vertically, or diagonally)
 * but cannot move into a position where it would be in check. The King is essential
 * for gameplay as its capture results in checkmate and the end of the game.
 *
 * Note: This implementation covers basic King movement. Special moves like castling
 * may be handled elsewhere in the chess engine.
 *
 */
public class King extends ChessPiece {

    /**
     * Constructs a new King at the specified position with the given color.
     *
     * @param row the initial row position (0-7) of the king
     * @param col the initial column position (0-7) of the king
     * @param color the color of the king (ChessPiece.WHITE or ChessPiece.BLACK)
     */
    public King(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     * Determines whether this king can legally move to the specified position.
     * A king can move if:
     * <ul>
     * <li>The destination is exactly one square away in any direction
     *     (horizontal, vertical, or diagonal)</li>
     * <li>The destination square is either empty or contains an opponent's piece</li>
     * <li>The move does not result in the king being in check</li>
     * </ul>
     *
     * The king cannot move more than one square at a time in normal movement,
     * and cannot move to a square that would put it in check.
     *
     * @param row the target row position (0-7)
     * @param col the target column position (0-7)
     * @param board the current state of the chess board
     * @return true if the king can legally move to the specified position, false otherwise
     */
    @Override
    public boolean canMoveTo(int row, int col, ChessBoard board) {
        int rowDiff = Math.abs(row - this.row);
        int colDiff = Math.abs(col - this.col);

        // TODO: Complete the polymorphic method canMoveTo()

        return !moveWouldCauseCheck(row, col, board);
    }

    /**
     * Returns the type of this chess piece.
     *
     * @return PieceType.KING indicating this is a king piece
     */
    @Override
    public PieceType getType() {
        return PieceType.KING;
    }
}