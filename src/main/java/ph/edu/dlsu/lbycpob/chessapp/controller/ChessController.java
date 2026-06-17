package ph.edu.dlsu.lbycpob.chessapp.controller;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import ph.edu.dlsu.lbycpob.chessapp.model.ChessBoard;
import ph.edu.dlsu.lbycpob.chessapp.model.EasyChessAI;
import ph.edu.dlsu.lbycpob.chessapp.model.GameLogic;
import ph.edu.dlsu.lbycpob.chessapp.model.pieces.*;
import ph.edu.dlsu.lbycpob.chessapp.view.AppView;

import java.util.ArrayList;
import java.util.List;


public class ChessController {
    private ChessBoard board;
    private final AppView view;
    private int currentPlayer = ChessPiece.WHITE;
    private ChessPiece selectedPiece = null;
    private boolean gameOver = false;
    private final boolean IS_OFFICIAL_NOTATION = true;
    private int moveNumber = 1;
    private final List<String> moveList = new ArrayList<>();

    private final int AI_COLOR = ChessPiece.BLACK;
    private final boolean AI_ENABLED = false;
    private boolean aiThinking = false;

    // Constructor
    public ChessController() {
        board = new ChessBoard();
        view = new AppView();
        view.updateBoard(board);
        setupEventHandlers();
        updateStatus();
    }

    private void setupEventHandlers() {
        view.getReplayButton().setOnAction(e -> resetGame());
        view.getBoard().setOnMouseClicked(this::handleMouseClick);
    }

    private void resetGame() {
        board = new ChessBoard();
        currentPlayer = ChessPiece.WHITE;
        selectedPiece = null;
        gameOver = false;
        aiThinking = false;
        moveNumber = 1;
        moveList.clear();

        GameLogic.resetCastleAndEnPassant();

        view.getMoveHistory().clear();
        view.getBoard().clearSelection();
        view.updateBoard(board);
        updateStatus();
    }

    private void handleMouseClick(MouseEvent event) {
        if (gameOver || aiThinking) return;

        if (AI_ENABLED && currentPlayer == AI_COLOR) return;

        int[] location = view.getLocation(event.getX(), event.getY());
        int row = location[0];
        int col = location[1];

        if (row < 0 || row >= 8 || col < 0 || col >= 8) return;

        if (selectedPiece == null) {
            // First click - select piece
            ChessPiece piece = board.pieceAt(row, col);
            if (piece != null && piece.getColor() == currentPlayer) {
                if (GameLogic.isInCheck(board, currentPlayer)) {
                    if (pieceCanHelpInCheck(piece)) {
                        selectedPiece = piece;
                        view.getBoard().selectSquare(row, col);
                    }
                    // If in check and the piece can't help, don't select it
                } else {
                    // Not in check - allow normal selection
                    selectedPiece = piece;
                    view.getBoard().selectSquare(row, col);
                }
            }
        } else {
            // Second click - attempt move
            if (row == selectedPiece.getRow() && col == selectedPiece.getCol()) {
                selectedPiece = null;
                view.getBoard().clearSelection();
            } else if (GameLogic.canMakeMove(board, selectedPiece, row, col)) {
                // Valid move - but double-check it resolves check if we're in check
                if (GameLogic.isInCheck(board, currentPlayer)) {
                    if (moveResolvesCheck(selectedPiece, row, col)) {
                        if (!IS_OFFICIAL_NOTATION) {
                            makeMove(selectedPiece, row, col);
                        } else {
                            makeOfficialMove(selectedPiece, row, col);
                        }
                    } else {
                        // Move doesn't resolve check - deselect
                        selectedPiece = null;
                        view.getBoard().clearSelection();
                    }
                } else {
                    // Not in check - proceed with move
                    if (!IS_OFFICIAL_NOTATION) {
                        makeMove(selectedPiece, row, col);
                    } else {
                        makeOfficialMove(selectedPiece, row, col);
                    }
                    if (AI_ENABLED && !gameOver && currentPlayer == AI_COLOR) {
                        scheduleAIMove();
                    }
                }
            } else {
                // Invalid move - deselect
                selectedPiece = null;
                view.getBoard().clearSelection();
            }
        }
    }

    /**
     * Check if a piece can help when the player is in check
     * (i.e., the piece has at least one legal move that gets out of check)
     */
    private boolean pieceCanHelpInCheck(ChessPiece piece) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (GameLogic.canMakeMove(board, piece, row, col) &&
                        moveResolvesCheck(piece, row, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if a move resolves the current check situation
     */
    private boolean moveResolvesCheck(ChessPiece piece, int newRow, int newCol) {
        // Simulate the move
        ChessPiece capturedPiece = board.pieceAt(newRow, newCol);
        int originalRow = piece.getRow();
        int originalCol = piece.getCol();

        // Handle special moves
        boolean isCastling = piece instanceof King && Math.abs(newCol - originalCol) == 2;
        boolean isEnPassant = piece instanceof Pawn && capturedPiece == null && originalCol != newCol;

        ChessPiece enPassantCaptured = null;
        ChessPiece movedRook = null;
        int rookOriginalCol = -1;
        int rookNewCol = -1;

        // Make temporary move
        board.removePiece(originalRow, originalCol);
        piece.moveTo(newRow, newCol);
        board.addPiece(piece);

        // Handle castling simulation
        if (isCastling) {
            boolean kingSide = newCol > originalCol;
            rookOriginalCol = kingSide ? 7 : 0;
            rookNewCol = kingSide ? 5 : 3;
            movedRook = board.pieceAt(newRow, rookOriginalCol);
            board.removePiece(newRow, rookOriginalCol);
            movedRook.moveTo(newRow, rookNewCol);
            board.addPiece(movedRook);
        }

        // Handle en passant simulation
        if (isEnPassant) {
            int capturedRow = (piece.getColor() == ChessPiece.WHITE) ? newRow + 1 : newRow - 1;
            enPassantCaptured = board.pieceAt(capturedRow, newCol);
            board.removePiece(capturedRow, newCol);
        }

        boolean resolvesCheck = !GameLogic.isInCheck(board, piece.getColor());

        // Undo the move
        board.removePiece(newRow, newCol);
        piece.moveTo(originalRow, originalCol);
        board.addPiece(piece);
        if (capturedPiece != null) {
            board.addPiece(capturedPiece);
        }

        // Undo castling
        if (isCastling && movedRook != null) {
            board.removePiece(newRow, rookNewCol);
            movedRook.moveTo(newRow, rookOriginalCol);
            board.addPiece(movedRook);
        }

        // Undo en passant
        if (isEnPassant && enPassantCaptured != null) {
            board.addPiece(enPassantCaptured);
        }

        return resolvesCheck;
    }

    private void scheduleAIMove() {
        aiThinking = true;
        updateStatus();

        Thread aiThread = new Thread(() -> {
            try {
                Thread.sleep(500); // AI Thinking time

                ChessBoard boardCopy = createBoardCopy(board);
                EasyChessAI.Move aiMove = EasyChessAI.getBestMove(boardCopy, AI_COLOR, 4, 5000);

                if (aiMove != null) {
                    Platform.runLater(() -> {
                        // FIX: Find the actual piece on the real board
                        ChessPiece actualPiece = board.pieceAt(aiMove.fromRow, aiMove.fromCol);
                        if (actualPiece != null && actualPiece.getColor() == AI_COLOR) {
                            executeAIMove(actualPiece, aiMove.toRow, aiMove.toCol);
                        }
                        aiThinking = false;
                        updateStatus();
                    });
                } else {
                    Platform.runLater(() -> {
                        aiThinking = false;
                        updateStatus();
                    });
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        aiThread.setDaemon(true);
        aiThread.start();
    }

    private void executeAIMove(ChessPiece piece, int newRow, int newCol) {
        if (!IS_OFFICIAL_NOTATION) {
            makeMove(piece, newRow, newCol);
        } else {
            makeOfficialMove(piece, newRow, newCol);
        }
    }

    private ChessBoard createBoardCopy(ChessBoard original) {
        ChessBoard copy = new ChessBoard();
        copy.clearBoard();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = original.pieceAt(row, col);
                if (piece != null) {
                    ChessPiece copiedPiece = copyPiece(piece);
                    copy.addPiece(copiedPiece);
                }
            }
        }
        return copy;
    }

    private ChessPiece copyPiece(ChessPiece original) {
        return switch (original.getType()) {
            case PAWN -> new Pawn(original.getRow(), original.getCol(), original.getColor());
            case ROOK -> new Rook(original.getRow(), original.getCol(), original.getColor());
            case KNIGHT -> new Knight(original.getRow(), original.getCol(), original.getColor());
            case BISHOP -> new Bishop(original.getRow(), original.getCol(), original.getColor());
            case QUEEN -> new Queen(original.getRow(), original.getCol(), original.getColor());
            case KING -> new King(original.getRow(), original.getCol(), original.getColor());
            default -> throw new IllegalArgumentException("Unknown piece type: " + original.getType());
        };
    }

    private void makeMove(ChessPiece piece, int newRow, int newCol) {
        int oldRow = piece.getRow();
        int oldCol = piece.getCol();
        ChessPiece capturedPiece = board.pieceAt(newRow, newCol);

        board.removePiece(oldRow, oldCol);
        piece.moveTo(newRow, newCol);
        board.addPiece(piece);

        String moveNotation = createMoveNotation(piece, oldRow, oldCol, newRow, newCol, capturedPiece);
        view.getMoveHistory().appendText(moveNotation + "\n");

        selectedPiece = null;
        view.getBoard().clearSelection();

        currentPlayer = (currentPlayer == ChessPiece.WHITE) ? ChessPiece.BLACK : ChessPiece.WHITE;

        view.updateBoard(board);
        updateStatus();

        checkGameState();
    }

    private void makeOfficialMove(ChessPiece piece, int newRow, int newCol) {
        int oldRow = piece.getRow();
        int oldCol = piece.getCol();
        ChessPiece capturedPiece = board.pieceAt(newRow, newCol);

        boolean isCastling = false;
        boolean isEnPassant = false;

        // Check for special moves
        if (piece instanceof King && Math.abs(newCol - oldCol) == 2) {
            isCastling = true;
        } else if (piece instanceof Pawn && capturedPiece == null && oldCol != newCol) {
            isEnPassant = true;
        }

        // Handle castling
        if (isCastling) {
            performCastling((King) piece, newRow, newCol);
        } else if (isEnPassant) {
            GameLogic.performEnPassant(board, (Pawn) piece, newRow, newCol, oldRow, oldCol);
        } else {
            // Regular move
            board.removePiece(oldRow, oldCol);
            piece.moveTo(newRow, newCol);
            board.addPiece(piece);
        }

        // Update castling flags
        GameLogic.updateCastlingFlags(piece, oldRow, oldCol);

        // Update en passant target
        GameLogic.updateEnPassantTarget(piece, oldRow, newRow, moveNumber);

        // Switch players before checking game state
        currentPlayer = (currentPlayer == ChessPiece.WHITE) ? ChessPiece.BLACK : ChessPiece.WHITE;

        // Record move with proper notation
        String moveNotation = createOfficialNotation(piece, oldRow, oldCol, newRow, newCol, capturedPiece, isCastling, isEnPassant);
        recordMove(moveNotation);

        // Clear selection
        selectedPiece = null;
        view.getBoard().clearSelection();

        // Update display
        view.updateBoard(board);
        updateStatus();

        // Check game state
        checkGameState();
    }

    private void performCastling(King king, int newRow, int newCol) {
        boolean kingSide = newCol > king.getCol();
        int rookOldCol = kingSide ? 7 : 0;
        int rookNewCol = kingSide ? 5 : 3;

        // Move king
        board.removePiece(king.getRow(), king.getCol());
        king.moveTo(newRow, newCol);
        board.addPiece(king);

        // Move rook
        ChessPiece rook = board.pieceAt(newRow, rookOldCol);
        board.removePiece(newRow, rookOldCol);
        rook.moveTo(newRow, rookNewCol);
        board.addPiece(rook);
    }

    private void recordMove(String moveNotation) {
        if (currentPlayer == ChessPiece.WHITE) {
            // Black just moved, complete the move pair
            if (!moveList.isEmpty()) {
                String lastMove = moveList.getLast();
                moveList.set(moveList.size() - 1, lastMove + " " + moveNotation);
                view.getMoveHistory().appendText(" " + moveNotation + " ");
            }
            moveNumber++;
        } else {
            // White just moved, start a new move pair
            String numberedMove = moveNumber + "." + moveNotation;
            moveList.add(numberedMove);
            view.getMoveHistory().appendText(numberedMove);
        }
    }

    private String createOfficialNotation(ChessPiece piece, int oldRow, int oldCol,
                                          int newRow, int newCol, ChessPiece capturedPiece,
                                          boolean isCastling, boolean isEnPassant) {
        if (isCastling) {
            return newCol > oldCol ? "O-O" : "O-O-O";
        }

        StringBuilder notation = new StringBuilder();

        // Get piece symbol (empty for pawn)
        String pieceSymbol = getPieceSymbol(piece.getType());

        // Handle pawn moves specially
        if (piece.getType() == PieceType.PAWN) {
            if (capturedPiece != null || isEnPassant) {
                // Pawn capture: file of departure + 'x' + destination
                notation.append(getFileFromCol(oldCol)).append("x");
            }
            // For pawn moves, no piece symbol is used
        } else {
            // Non-pawn pieces
            notation.append(pieceSymbol);

            // Add disambiguation if needed
            String disambiguation = getDisambiguation(piece, oldRow, oldCol, newRow, newCol);
            notation.append(disambiguation);

            // Add capture symbol if capturing
            if (capturedPiece != null) {
                notation.append("x");
            }
        }

        // Add destination square
        notation.append(getSquareNotation(newRow, newCol));

        // Check for special moves
        if (isPromotion(piece, newRow)) {
            // Add promotion notation (assuming promotion to Queen for now)
            notation.append("=♛");
        }

        // Add en passant notation
        if (isEnPassant) {
            notation.append(" e.p.");
        }

        // Add check/checkmate notation
        String checkNotation = getCheckNotation();
        notation.append(checkNotation);

        return notation.toString();
    }

    private String getDisambiguation(ChessPiece piece, int oldRow, int oldCol, int newRow, int newCol) {
        List<ChessPiece> samePieces = new ArrayList<>();

        // Find all pieces of same type and color that can move to the destination
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece otherPiece = board.pieceAt(row, col);
                if (otherPiece != null &&
                        otherPiece != piece &&
                        otherPiece.getType() == piece.getType() &&
                        otherPiece.getColor() == piece.getColor() &&
                        otherPiece.canMoveTo(newRow, newCol, board)) {
                    samePieces.add(otherPiece);
                }
            }
        }

        if (samePieces.isEmpty()) {
            return ""; // No disambiguation needed
        }

        // Check if file disambiguation is sufficient
        boolean needFileDisambiguation = false;
        boolean needRankDisambiguation = false;

        for (ChessPiece otherPiece : samePieces) {
            if (otherPiece.getCol() == oldCol) {
                needRankDisambiguation = true;
            }
            if (otherPiece.getRow() != oldRow) {
                needFileDisambiguation = true;
            }
        }

        if (needFileDisambiguation && !needRankDisambiguation) {
            return getFileFromCol(oldCol);
        } else if (needRankDisambiguation && !needFileDisambiguation) {
            return getRankFromRow(oldRow);
        } else if (needFileDisambiguation && needRankDisambiguation) {
            return getFileFromCol(oldCol) + getRankFromRow(oldRow);
        } else {
            return getFileFromCol(oldCol); // Default to file
        }
    }

    // Helper methods remain the same
    private String createMoveNotation(ChessPiece piece, int oldRow, int oldCol,
                                      int newRow, int newCol, ChessPiece capturedPiece) {
        String colorName = (piece.getColor() == ChessPiece.WHITE) ? "White" : "Black";
        String pieceName = piece.getType().toString().toLowerCase();
        String capture = (capturedPiece != null) ? " captures " : " moves from ";

        return colorName + " " + pieceName + capture +
                "(" + oldRow + "," + oldCol + ") to " +
                "(" + newRow + "," + newCol + ")";
    }

    private void updateStatus() {
        if (gameOver) return;

        String playerName = (currentPlayer == ChessPiece.WHITE) ? "White" : "Black";
        String status = playerName + " to move";

        if (aiThinking && currentPlayer == AI_COLOR) {
            status = "AI is thinking...";
        } else if (GameLogic.isInCheck(board, currentPlayer)) {
            status += " (in check)";
        }

        view.getStatusText().setText(status);
    }

    private void checkGameState() {
        if (GameLogic.isInCheckmate(board, currentPlayer)) {
            String winner = (currentPlayer == ChessPiece.WHITE) ? "Black" : "White";
            if (AI_ENABLED) {
                if (currentPlayer == AI_COLOR) {
                    view.getStatusText().setText("You won! AI is in checkmate.");
                } else {
                    view.getStatusText().setText("AI won! You are in checkmate.");
                }
            } else {
                view.getStatusText().setText("Checkmate! " + winner + " wins!");
            }
            gameOver = true;
        } else if (GameLogic.isInStalemate(board, currentPlayer)) {
            view.getStatusText().setText("Stalemate! Game is a draw.");
            gameOver = true;
        }
    }

    private String getPieceSymbol(PieceType type) {
        return view.getBoard().getPieceSymbol(type);
    }

    private String getFileFromCol(int col) {
        return String.valueOf((char) ('a' + col));
    }

    private String getRankFromRow(int row) {
        return String.valueOf(8 - row);
    }

    private String getSquareNotation(int row, int col) {
        return getFileFromCol(col) + getRankFromRow(row);
    }

    private boolean isPromotion(ChessPiece piece, int newRow) {
        if (piece.getType() != PieceType.PAWN) {
            return false;
        }

        return (piece.getColor() == ChessPiece.WHITE && newRow == 0) ||
                (piece.getColor() == ChessPiece.BLACK && newRow == 7);
    }

    private String getCheckNotation() {
        int opponentColor = (currentPlayer == ChessPiece.WHITE) ? ChessPiece.BLACK : ChessPiece.WHITE;

        if (GameLogic.isInCheckmate(board, opponentColor)) {
            return "#";
        } else if (GameLogic.isInCheck(board, opponentColor)) {
            return "+";
        }
        return "";
    }

    public BorderPane getView() {
        return view.getRoot();
    }
}