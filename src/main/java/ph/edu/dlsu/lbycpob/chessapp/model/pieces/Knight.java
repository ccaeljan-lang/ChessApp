package ph.edu.dlsu.lbycpob.chessapp.model.pieces;

import ph.edu.dlsu.lbycpob.chessapp.model.ChessBoard;

/**
 * Represents a Knight chess piece that moves in an L-shaped pattern.
 * The Knight is unique among chess pieces as it can "jump" over other pieces
 * and is the only piece that does not move in straight lines. It moves exactly
 * two squares in one direction (horizontal or vertical) and then one square
 * perpendicular to that direction, forming an L-shape.
 * <p>
 * The Knight has eight possible moves from any position (unless blocked by
 * board boundaries): two squares up/down + one square left/right, or
 * two squares left/right + one square up/down.
 */
public class Knight extends ChessPiece {

    /**
     * Constructs a new Knight at the specified position with the given color.
     *
     * @param row   the initial row position (0-7) of the knight
     * @param col   the initial column position (0-7) of the knight
     * @param color the color of the knight (ChessPiece.WHITE or ChessPiece.BLACK)
     */
    public Knight(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     * Determines whether this knight can legally move to the specified position.
     * A knight can move if:
     * <ul>
     * <li>The move forms an L-shape: exactly 2 squares in one direction
     *     and 1 square perpendicular to that direction</li>
     * <li>The destination square is either empty or contains an opponent's piece</li>
     * <li>The move does not leave the king in check</li>
     * </ul>
     * <p>
     * Valid L-shaped moves are:
     * <ul>
     * <li>2 rows up/down + 1 column left/right</li>
     * <li>1 row up/down + 2 columns left/right</li>
     * </ul>
     * <p>
     * Unlike other pieces, the knight can jump over pieces in its path and
     * only the destination square needs to be checked for occupancy.
     *
     * @param row   the target row position (0-7)
     * @param col   the target column position (0-7)
     * @param board the current state of the chess board
     * @return true if the knight can legally move to the specified position, false otherwise
     */
    @Override
    public boolean canMoveTo(int row, int col, ChessBoard board) {
        int rowDiff = Math.abs(row - this.row);
        int colDiff = Math.abs(col - this.col);

        // L-shape move

        // TODO: Complete the polymorphic method canMoveTo()

        // Check destination

        // TODO: Complete the polymorphic method canMoveTo()

        return !moveWouldCauseCheck(row, col, board);
    }

    /**
     * Returns the type of this chess piece.
     *
     * @return PieceType.KNIGHT indicating this is a knight piece
     */
    @Override
    public PieceType getType() {
        return PieceType.KNIGHT;
    }
}