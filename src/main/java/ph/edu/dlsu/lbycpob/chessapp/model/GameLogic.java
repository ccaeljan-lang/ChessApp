package ph.edu.dlsu.lbycpob.chessapp.model;

import ph.edu.dlsu.lbycpob.chessapp.model.pieces.ChessPiece;
import ph.edu.dlsu.lbycpob.chessapp.model.pieces.King;
import ph.edu.dlsu.lbycpob.chessapp.model.pieces.Pawn;
import ph.edu.dlsu.lbycpob.chessapp.model.pieces.Rook;

public class GameLogic {

    // For en passant
    private static Pawn enPassantTarget = null;
    private static int enPassantMoveNumber = -1;

    // For castling
    private static boolean whiteKingMoved = false;
    private static boolean blackKingMoved = false;
    private static boolean whiteKingsideRookMoved = false;
    private static boolean whiteQueensideRookMoved = false;
    private static boolean blackKingsideRookMoved = false;
    private static boolean blackQueensideRookMoved = false;

    public static void resetCastleAndEnPassant() {
        // Reset castling and en passant flags
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteKingsideRookMoved = false;
        whiteQueensideRookMoved = false;
        blackKingsideRookMoved = false;
        blackQueensideRookMoved = false;
        enPassantTarget = null;
        enPassantMoveNumber = -1;
    }

    public static boolean isInCheck(ChessBoard board, int playerColor) {
        // Find the king
        King king = null;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.pieceAt(row, col);
                if (piece instanceof King && piece.getColor() == playerColor) {
                    king = (King) piece;
                    break;
                }
            }
        }

        if (king == null) return false;

        // Check if any opponent piece can attack the king
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.pieceAt(row, col);
                if (piece != null && piece.getColor() != playerColor) {
                    if (canAttackSquare(piece, king.getRow(), king.getCol(), board)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean canAttackSquare(ChessPiece piece, int targetRow, int targetCol, ChessBoard board) {
        // Similar to canMoveTo but without check for causing check (to avoid infinite recursion)
        if (piece instanceof Pawn) {
            int direction = (piece.getColor() == ChessPiece.WHITE) ? -1 : 1;
            int rowDiff = targetRow - piece.getRow();
            int colDiff = Math.abs(targetCol - piece.getCol());
            return rowDiff == direction && colDiff == 1;
        }

        // For other pieces, use simplified movement rules
        return piece.canMoveTo(targetRow, targetCol, board);
    }

    public static boolean isInCheckmate(ChessBoard board, int playerColor) {
        if (!isInCheck(board, playerColor)) {
            return false;
        }

        // Check if any move can get out of check
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.pieceAt(row, col);
                if (piece != null && piece.getColor() == playerColor) {
                    for (int newRow = 0; newRow < 8; newRow++) {
                        for (int newCol = 0; newCol < 8; newCol++) {
                            if (piece.canMoveTo(newRow, newCol, board)) {
                                return false; // Found a legal move
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public static boolean isInStalemate(ChessBoard board, int playerColor) {
        if (isInCheck(board, playerColor)) {
            return false; // In check, not stalemate
        }

        // Check if any legal move exists
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.pieceAt(row, col);
                if (piece != null && piece.getColor() == playerColor) {
                    for (int newRow = 0; newRow < 8; newRow++) {
                        for (int newCol = 0; newCol < 8; newCol++) {
                            if (piece.canMoveTo(newRow, newCol, board)) {
                                return false; // Found a legal move
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public static boolean canMakeMove(ChessBoard board, ChessPiece piece, int newRow, int newCol) {
        // Check basic move validity
        if (!piece.canMoveTo(newRow, newCol, board)) {
            // Check for castling
            if (piece instanceof King && canCastle(board, (King) piece, newRow, newCol)) {
                return true;
            }
            // Check for en passant
            return piece instanceof Pawn && canEnPassant((Pawn) piece, newRow, newCol);
        }
        return true;
    }

    public static boolean canCastle(ChessBoard board, King king, int newRow, int newCol) {
        // King must not have moved
        if ((king.getColor() == ChessPiece.WHITE && whiteKingMoved) ||
                (king.getColor() == ChessPiece.BLACK && blackKingMoved)) {
            return false;
        }

        // Must be moving to castling positions
        if (newRow != king.getRow()) return false;

        int colDiff = newCol - king.getCol();
        if (Math.abs(colDiff) != 2) return false;

        // Check if rook is available
        boolean kingSide = colDiff > 0;
        int rookCol = kingSide ? 7 : 0;
        ChessPiece rook = board.pieceAt(king.getRow(), rookCol);

        if (!(rook instanceof Rook) || rook.getColor() != king.getColor()) {
            return false;
        }

        // Check if rook has moved
        if (king.getColor() == ChessPiece.WHITE) {
            if ((kingSide && whiteKingsideRookMoved) || (!kingSide && whiteQueensideRookMoved)) {
                return false;
            }
        } else {
            if ((kingSide && blackKingsideRookMoved) || (!kingSide && blackQueensideRookMoved)) {
                return false;
            }
        }

        // Check path is clear
        int start = Math.min(king.getCol(), rookCol) + 1;
        int end = Math.max(king.getCol(), rookCol);
        for (int col = start; col < end; col++) {
            if (board.pieceAt(king.getRow(), col) != null) {
                return false;
            }
        }

        // Check king is not in check and doesn't pass through check
        if (GameLogic.isInCheck(board, king.getColor())) {
            return false;
        }

        // Simulate king movement through intermediate square
        int intermediateCol = king.getCol() + (kingSide ? 1 : -1);
        ChessPiece originalPiece = board.pieceAt(king.getRow(), intermediateCol);
        board.removePiece(king.getRow(), king.getCol());
        king.moveTo(king.getRow(), intermediateCol);
        board.addPiece(king);

        boolean passesCheck = !GameLogic.isInCheck(board, king.getColor());

        // Restore position
        board.removePiece(king.getRow(), intermediateCol);
        king.moveTo(king.getRow(), king.getCol());
        board.addPiece(king);
        if (originalPiece != null) {
            board.addPiece(originalPiece);
        }

        return passesCheck;
    }

    public static boolean canEnPassant(Pawn pawn, int newRow, int newCol) {
        if (enPassantTarget == null) return false;

        // Must be diagonal move
        int rowDiff = newRow - pawn.getRow();
        int colDiff = Math.abs(newCol - pawn.getCol());
        int direction = (pawn.getColor() == ChessPiece.WHITE) ? -1 : 1;

        if (rowDiff != direction || colDiff != 1) return false;

        // Target square must be where en passant pawn can be captured
        return newRow == enPassantTarget.getRow() + direction &&
                newCol == enPassantTarget.getCol();
    }

    public static void updateCastlingFlags(ChessPiece piece, int oldRow, int oldCol) {
        if (piece instanceof King) {
            if (piece.getColor() == ChessPiece.WHITE) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        } else if (piece instanceof Rook) {
            if (piece.getColor() == ChessPiece.WHITE) {
                if (oldRow == 7 && oldCol == 0) whiteQueensideRookMoved = true;
                if (oldRow == 7 && oldCol == 7) whiteKingsideRookMoved = true;
            } else {
                if (oldRow == 0 && oldCol == 0) blackQueensideRookMoved = true;
                if (oldRow == 0 && oldCol == 7) blackKingsideRookMoved = true;
            }
        }
    }

    public static void updateEnPassantTarget(ChessPiece piece, int oldRow, int newRow, int moveNumber) {
        enPassantTarget = null;

        if (piece instanceof Pawn && Math.abs(newRow - oldRow) == 2) {
            enPassantTarget = (Pawn) piece;
            enPassantMoveNumber = moveNumber;
        }
    }

    public static void performEnPassant(ChessBoard board, Pawn pawn, int newRow, int newCol, int oldRow, int oldCol) {
        // Move pawn
        board.removePiece(oldRow, oldCol);
        pawn.moveTo(newRow, newCol);
        board.addPiece(pawn);

        // Remove captured pawn
        board.removePiece(enPassantTarget.getRow(), enPassantTarget.getCol());
    }

    private GameLogic() {
        // Not meant for instantiation
    }
}