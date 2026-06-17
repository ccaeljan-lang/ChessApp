package ph.edu.dlsu.lbycpob.chessapp.model.pieces;

import ph.edu.dlsu.lbycpob.chessapp.model.ChessBoard;

/**
 * Represents a Bishop chess piece that moves diagonally across the board.
 * A Bishop can move any number of squares diagonally in any direction
 * (northeast, northwest, southeast, southwest) as long as the path is clear
 * and the destination square is either empty or contains an opponent's piece.
 *
 */
public class Bishop extends ChessPiece {

    /**
     * Constructs a new Bishop at the specified position with the given color.
     *
     * @param row the initial row position (0-7) of the bishop
     * @param col the initial column position (0-7) of the bishop
     * @param color the color of the bishop (ChessPiece.WHITE or ChessPiece.BLACK)
     */
    public Bishop(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     * Determines whether this bishop can legally move to the specified position.
     * A bishop can move if:
     * <ul>
     * <li>The move is diagonal (absolute row difference equals absolute column difference)</li>
     * <li>All squares along the diagonal path are empty</li>
     * <li>The destination square is either empty or contains an opponent's piece</li>
     * <li>The move does not leave the king in check</li>
     * </ul>
     *
     * @param row the target row position (0-7)
     * @param col the target column position (0-7)
     * @param board the current state of the chess board
     * @return true if the bishop can legally move to the specified position, false otherwise
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
     * @return PieceType.BISHOP indicating this is a bishop piece
     */
    @Override
    public PieceType getType() {
        return PieceType.BISHOP;
    }
}