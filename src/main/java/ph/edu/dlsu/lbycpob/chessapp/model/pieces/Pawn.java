package ph.edu.dlsu.lbycpob.chessapp.model.pieces;

import ph.edu.dlsu.lbycpob.chessapp.model.ChessBoard;

/**
 * Represents a Pawn chess piece with unique movement and capture rules.
 * Pawns have the most complex movement rules among chess pieces:
 * <ul>
 * <li>Move forward one square if the square is empty</li>
 * <li>Move forward two squares on their first move if both squares are empty</li>
 * <li>Capture diagonally forward one square if an opponent's piece is present</li>
 * <li>Cannot move backward</li>
 * </ul>
 *
 * White pawns move "up" the board (decreasing row numbers) while black pawns
 * move "down" the board (increasing row numbers). Pawns are the only pieces
 * that move and capture differently.
 *
 * Note: Special moves like en passant and pawn promotion may be handled
 * elsewhere in the chess engine.
 *
 */
public class Pawn extends ChessPiece {

    /**
     * Tracks whether this pawn has moved from its starting position.
     * Used to determine if the pawn can make its initial two-square move.
     */
    private boolean hasMoved = false;

    /**
     * Constructs a new Pawn at the specified position with the given color.
     * The pawn starts with hasMoved set to false, allowing for the initial
     * two-square move.
     *
     * @param row the initial row position (0-7) of the pawn
     * @param col the initial column position (0-7) of the pawn
     * @param color the color of the pawn (ChessPiece.WHITE or ChessPiece.BLACK)
     */
    public Pawn(int row, int col, int color) {
        super(row, col, color);
    }

    /** GIVEN AS EXAMPLE
     * Determines whether this pawn can legally move to the specified position.
     * A pawn can move if one of the following conditions is met:
     * <ul>
     * <li><strong>Forward one square:</strong> The destination is one square forward
     *     in the same column and is empty</li>
     * <li><strong>Forward two squares (initial move):</strong> The pawn hasn't moved yet,
     *     the destination is two squares forward in the same column, and both squares are empty</li>
     * <li><strong>Diagonal capture:</strong> The destination is one square diagonally forward
     *     and contains an opponent's piece</li>
     * <li>The move does not leave the king in check</li>
     * </ul>
     *
     * Movement direction is determined by color: white pawns move toward row 0,
     * black pawns move toward row 7.
     *
     * @param row the target row position (0-7)
     * @param col the target column position (0-7)
     * @param board the current state of the chess board
     * @return true if the pawn can legally move to the specified position, false otherwise
     */
    @Override
    public boolean canMoveTo(int row, int col, ChessBoard board) {
        int rowDiff = row - this.row;
        int colDiff = col - this.col;

        // Direction depends on color
        int direction = (color == WHITE) ? -1 : 1;

        // Forward move
        if (colDiff == 0) {
            if (rowDiff == direction && board.pieceAt(row, col) == null) {
                return !moveWouldCauseCheck(row, col, board);
            }
            // Initial two-square move
            if (!hasMoved && rowDiff == 2 * direction && board.pieceAt(row, col) == null) {
                return !moveWouldCauseCheck(row, col, board);
            }
        }

        // Diagonal capture
        if (Math.abs(colDiff) == 1 && rowDiff == direction) {
            ChessPiece target = board.pieceAt(row, col);
            if (target != null && target.getColor() != this.color) {
                return !moveWouldCauseCheck(row, col, board);
            }
        }
        return false;
    }

    /**
     * Moves this pawn to the specified position and marks it as having moved.
     * After the first move, the pawn can no longer make the initial two-square move.
     * This method overrides the parent implementation to update the hasMoved flag.
     *
     * @param row the target row position (0-7)
     * @param col the target column position (0-7)
     */
    @Override
    public void moveTo(int row, int col) {
        super.moveTo(row, col);
        hasMoved = true;
    }

    /**
     * Returns the type of this chess piece.
     *
     * @return PieceType.PAWN indicating this is a pawn piece
     */
    @Override
    public PieceType getType() {
        return PieceType.PAWN;
    }
}