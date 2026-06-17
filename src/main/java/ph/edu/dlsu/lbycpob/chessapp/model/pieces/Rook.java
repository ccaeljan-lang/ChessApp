package ph.edu.dlsu.lbycpob.chessapp.model.pieces;

import ph.edu.dlsu.lbycpob.chessapp.model.ChessBoard;

/**
 * Represents a Rook chess piece that moves in straight lines.
 * The Rook can move any number of squares horizontally (along ranks) or
 * vertically (along files) as long as the path is clear. It cannot move
 * diagonally and cannot jump over other pieces. The Rook is one of the
 * major pieces in chess and is particularly powerful in open positions
 * and endgames.
 *
 * The Rook is also involved in the special castling move with the King,
 * though that functionality may be handled elsewhere in the chess engine.
 *
 */
public class Rook extends ChessPiece {

    /**
     * Constructs a new Rook at the specified position with the given color.
     *
     * @param row the initial row position (0-7) of the rook
     * @param col the initial column position (0-7) of the rook
     * @param color the color of the rook (ChessPiece.WHITE or ChessPiece.BLACK)
     */
    public Rook(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     * Determines whether this rook can legally move to the specified position.
     * A rook can move if:
     * <ul>
     * <li>The move is either horizontal (same row) or vertical (same column)</li>
     * <li>All squares along the path between current position and destination are empty</li>
     * <li>The destination square is either empty or contains an opponent's piece</li>
     * <li>The move does not leave the king in check</li>
     * </ul>
     *
     * The rook moves in straight lines only:
     * <ul>
     * <li><strong>Horizontal movement:</strong> along the same row (rank)</li>
     * <li><strong>Vertical movement:</strong> along the same column (file)</li>
     * </ul>
     *
     * @param row the target row position (0-7)
     * @param col the target column position (0-7)
     * @param board the current state of the chess board
     * @return true if the rook can legally move to the specified position, false otherwise
     */
    @Override
    public boolean canMoveTo(int row, int col, ChessBoard board) {
        if (row != this.row && col != this.col) {
            return false; // Must move horizontally or vertically
        }

        // Check path is clear
        // TODO: Complete the polymorphic method canMoveTo()

        // Check destination
        // TODO: Complete the polymorphic method canMoveTo()

        return !moveWouldCauseCheck(row, col, board);
    }

    /**
     * Returns the type of this chess piece.
     *
     * @return PieceType.ROOK indicating this is a rook piece
     */
    @Override
    public PieceType getType() {
        return PieceType.ROOK;
    }
}