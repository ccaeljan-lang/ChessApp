package ph.edu.dlsu.lbycpob.chessapp.model;

// ChessBoard.java
import ph.edu.dlsu.lbycpob.chessapp.model.pieces.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Represents a chess board that manages the placement and retrieval of chess pieces.
 * The board is implemented as an 8x8 grid where each position can contain a ChessPiece
 * or be empty (null). The board follows standard chess positioning with row 0 being
 * the black back rank and row 7 being the white back rank.
 */
public class ChessBoard implements ChessBoardInterface {

    /**
     * The 8x8 grid representing the chess board.
     * Each element can contain a ChessPiece or null for empty squares.
     */
    private ChessPiece[][] board;

    /**
     * Constructs a new ChessBoard and initializes it with pieces in their
     * standard starting positions.
     */
    public ChessBoard() {
        board = new ChessPiece[8][8];
        initializeBoard();
    }

    /**
     * Retrieves the chess piece at the specified position on the board.
     *
     * @param row the row coordinate (0-7, where 0 is the top row)
     * @param col the column coordinate (0-7, where 0 is the leftmost column)
     * @return the ChessPiece at the specified position, or null if the position
     *         is empty or the coordinates are out of bounds
     */
    @Override
    public ChessPiece pieceAt(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            return board[row][col];
        }
        return null;
    }

    /**
     * Adds a chess piece to the board at the position specified by the piece's
     * row and column coordinates.
     *
     * @param piece the ChessPiece to add to the board. If null, no action is taken.
     *              The piece's getRow() and getCol() methods determine its position.
     */
    @Override
    public void addPiece(ChessPiece piece) {
        if (piece != null) {
            board[piece.getRow()][piece.getCol()] = piece;
        }
    }

    /**
     * Removes the chess piece at the specified position by setting that
     * position to null.
     *
     * @param row the row coordinate (0-7) of the piece to remove
     * @param col the column coordinate (0-7) of the piece to remove
     */
    @Override
    public void removePiece(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            board[row][col] = null;
        }
    }

    /**
     * Initializes the chess board with all pieces in their standard starting
     * positions according to chess rules. Black pieces are placed on rows 0-1
     * and white pieces are placed on rows 6-7.
     *
     * The setup includes:
     * - Black pieces: back rank (row 0) with major pieces, pawns on row 1
     * - White pieces: pawns on row 6, back rank (row 7) with major pieces
     */
    public void initializeBoard() {
        // PLAYER 1 (WHITE)
        // Back row pieces.
        addPiece(new Rook(7, 0, ChessPiece.WHITE));
        addPiece(new Knight(7, 1, ChessPiece.WHITE));
        addPiece(new Bishop(7, 2, ChessPiece.WHITE));
        addPiece(new Queen(7, 3, ChessPiece.WHITE));
        addPiece(new King(7, 4, ChessPiece.WHITE));
        addPiece(new Bishop(7, 5, ChessPiece.WHITE));
        addPiece(new Knight(7, 6, ChessPiece.WHITE));
        addPiece(new Rook(7, 7, ChessPiece.WHITE));
        // Pawn Pieces
        addPiece(new Pawn(6, 0, ChessPiece.WHITE));
        addPiece(new Pawn(6, 1, ChessPiece.WHITE));
        addPiece(new Pawn(6, 2, ChessPiece.WHITE));
        addPiece(new Pawn(6, 3, ChessPiece.WHITE));
        addPiece(new Pawn(6, 4, ChessPiece.WHITE));
        addPiece(new Pawn(6, 5, ChessPiece.WHITE));
        addPiece(new Pawn(6, 6, ChessPiece.WHITE));
        addPiece(new Pawn(6, 7, ChessPiece.WHITE));

        // PLAYER 2 (BLACK)
        // Back row pieces.
        addPiece(new Rook(0, 0, ChessPiece.BLACK));
        addPiece(new Knight(0, 1, ChessPiece.BLACK));
        addPiece(new Bishop(0, 2, ChessPiece.BLACK));
        addPiece(new Queen(0, 3, ChessPiece.BLACK));
        addPiece(new King(0, 4, ChessPiece.BLACK));
        addPiece(new Bishop(0, 5, ChessPiece.BLACK));
        addPiece(new Knight(0, 6, ChessPiece.BLACK));
        addPiece(new Rook(0, 7, ChessPiece.BLACK));
        // Pawn Pieces
        addPiece(new Pawn(1, 0, ChessPiece.BLACK));
        addPiece(new Pawn(1, 1, ChessPiece.BLACK));
        addPiece(new Pawn(1, 2, ChessPiece.BLACK));
        addPiece(new Pawn(1, 3, ChessPiece.BLACK));
        addPiece(new Pawn(1, 4, ChessPiece.BLACK));
        addPiece(new Pawn(1, 5, ChessPiece.BLACK));
        addPiece(new Pawn(1, 6, ChessPiece.BLACK));
        addPiece(new Pawn(1, 7, ChessPiece.BLACK));
    }

    /**
     * Removes all pieces from the board by setting every position to null.
     * This results in an empty 8x8 board.
     */
    public void clearBoard() {
        for (ChessPiece[] row : board) {
            Arrays.fill(row, null);
        }
    }

    public List<int[]> getValidMoves(ChessPiece piece) {
        List<int[]> validMoves = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (piece.canMoveTo(row, col, this)) {
                    validMoves.add(new int[]{row, col});
                }
            }
        }

        return validMoves;
    }

    /**
     * Sets the board state from a provided 2D array of ChessPieces.
     * This method copies the pieces from the input array to the board,
     * allowing for custom board configurations.
     *
     * @param pieces a 2D array of ChessPieces representing the desired board state.
     *               The array should be properly sized to match the board dimensions.
     *               Null elements represent empty squares.
     */
    public void setBoardFromArray(ChessPiece[][] pieces) {
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                board[i][j] = pieces[i][j];
            }
        }
    }
}