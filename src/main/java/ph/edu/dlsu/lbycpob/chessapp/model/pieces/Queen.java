package ph.edu.dlsu.lbycpob.chessapp.model.pieces;

import ph.edu.dlsu.lbycpob.chessapp.model.ChessBoard;

/**
 * Represents a Queen chess piece, the most powerful piece on the board.
 * The Queen combines the movement capabilities of both the Rook and Bishop,
 * allowing it to move any number of squares in eight directions:
 * horizontally, vertically, and diagonally. This makes the Queen the most
 * versatile and valuable piece in chess, second only to the King in importance.
 *
 * The Queen can move across the entire board in a single move, provided
 * the path is clear of other pieces. It can capture opponent pieces by
 * moving to their square, but cannot jump over pieces like the Knight.
 *
 */
public class Queen extends ChessPiece {

    /**
     * Constructs a new Queen at the specified position with the given color.
     *
     * @param row the initial row position (0-7) of the queen
     * @param col the initial column position (0-7) of the queen
     * @param color the color of the queen (ChessPiece.WHITE or ChessPiece.BLACK)
     */
    public Queen(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     * Determines whether this queen can legally move to the specified position.
     * A queen can move if:
     * <ul>
     * <li>The move is either straight (like a rook: same row or column) or
     *     diagonal (like a bishop: equal row and column differences)</li>
     * <li>All squares along the path between current position and destination are empty</li>
     * <li>The destination square is either empty or contains an opponent's piece</li>
     * <li>The move does not leave the king in check</li>
     * </ul>
     *
     * The queen essentially combines rook and bishop movement patterns:
     * <ul>
     * <li><strong>Rook-like movement:</strong> horizontally or vertically any number of squares</li>
     * <li><strong>Bishop-like movement:</strong> diagonally any number of squares</li>
     * </ul>
     *
     * @param row the target row position (0-7)
     * @param col the target column position (0-7)
     * @param board the current state of the chess board
     * @return true if the queen can legally move to the specified position, false otherwise
     */
    @Override
    public boolean canMoveTo(int row, int col, ChessBoard board) {
        int rowDiff = Math.abs(row - this.row);
        int colDiff = Math.abs(col - this.col);

        // Must move like rook or bishop
        // TODO: Complete the polymorphic method canMoveTo()

        // Check path is clear
        // TODO: Complete the polymorphic method canMoveTo()

        // Check destination
        // TODO: Complete the polymorphic method canMoveTo()

        return !moveWouldCauseCheck(row, col, board);
    }

    /**
     * Returns the type of this chess piece.
     *
     * @return PieceType.QUEEN indicating this is a queen piece
     */
    @Override
    public PieceType getType() {
        return PieceType.QUEEN;
    }
}