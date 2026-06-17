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

        // [UNDERSTAND] A queen can move like a rook
        // (horizontal/vertical) or a bishop (diagonal).
        if (!(row == this.row || col == this.col || rowDiff == colDiff)) {
            return false;
        }

        // [UNDERSTAND] Check that every square between the queen
        // and the destination is empty.
        if (row == this.row) { // Horizontal move
            int step = (col > this.col) ? 1 : -1;

            for (int c = this.col + step; c != col; c += step) {
                if (board.pieceAt(row, c) != null) {
                    return false;
                }
            }
        }
        else if (col == this.col) { // Vertical move
            int step = (row > this.row) ? 1 : -1;

            for (int r = this.row + step; r != row; r += step) {
                if (board.pieceAt(r, col) != null) {
                    return false;
                }
            }
        }
        else { // Diagonal move
            int rowStep = (row > this.row) ? 1 : -1;
            int colStep = (col > this.col) ? 1 : -1;

            int currentRow = this.row + rowStep;
            int currentCol = this.col + colStep;

            while (currentRow != row && currentCol != col) {
                if (board.pieceAt(currentRow, currentCol) != null) {
                    return false;
                }

                currentRow += rowStep;
                currentCol += colStep;
            }
        }

        // [UNDERSTAND] Check destination.
        ChessPiece targetPiece = board.pieceAt(row, col);

        // [UNDERSTAND] Cannot capture your own piece.
        if (targetPiece != null && targetPiece.getColor() == this.color) {
            return false;
        }

        // [UNDERSTAND] To know if a move is a check or not.
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