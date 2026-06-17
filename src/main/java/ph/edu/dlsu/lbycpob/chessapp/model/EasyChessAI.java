package ph.edu.dlsu.lbycpob.chessapp.model;

import ph.edu.dlsu.lbycpob.chessapp.model.pieces.*;

import java.util.ArrayList;
import java.util.List;

public class EasyChessAI {
    private static final int MAX_DEPTH = 4;
    private static final int INFINITY = 1000000;

    private static final int[] PIECE_VALUES = {
            100,  // PAWN
            500,  // ROOK
            300,  // KNIGHT
            300,  // BISHOP
            900,  // QUEEN
            10000 // KING
    };

    private static final int[][] PAWN_TABLE = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    private static final int[][] KNIGHT_TABLE = {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}
    };

    private static final int[][] BISHOP_TABLE = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}
    };

    private static final int[][] KING_TABLE = {
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            {20, 20, 0, 0, 0, 0, 20, 20},
            {20, 30, 10, 0, 0, 10, 30, 20}
    };

    public static class Move {
        public final ChessPiece piece;
        public final int fromRow, fromCol;
        public final int toRow, toCol;
        public final ChessPiece capturedPiece;
        public final boolean isCastling;
        public final boolean isEnPassant;
        public final boolean isPromotion;

        public Move(ChessPiece piece, int fromRow, int fromCol, int toRow, int toCol, ChessPiece capturedPiece) {
            this.piece = piece;
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
            this.capturedPiece = capturedPiece;
            this.isCastling = piece instanceof King && Math.abs(toCol - fromCol) == 2;
            this.isEnPassant = piece instanceof Pawn && capturedPiece == null && fromCol != toCol;
            this.isPromotion = piece instanceof Pawn && ((piece.getColor() == ChessPiece.WHITE && toRow == 0) ||
                    (piece.getColor() == ChessPiece.BLACK && toRow == 7));
        }
    }

    public static Move getBestMove(ChessBoard board, int aiColor, int depth, long timeLimit) {
        long startTime = System.currentTimeMillis();
        return minimaxRoot(board, aiColor, depth, -INFINITY, INFINITY, startTime, timeLimit);
    }

    private static Move minimaxRoot(ChessBoard board, int aiColor, int depth, int alpha, int beta, long startTime, long timeLimit) {
        List<Move> moves = generateAllMoves(board, aiColor);
        if (moves.isEmpty()) return null;

        Move bestMove = null;
        int bestValue = -INFINITY;

        // Sort moves for better pruning - prioritize captures and important moves
        moves.sort((m1, m2) -> Integer.compare(getMoveScore(m2), getMoveScore(m1)));

        for (Move move : moves) {
            if (System.currentTimeMillis() - startTime > timeLimit) break;

            // Create board copy and make move
            ChessBoard tempBoard = createBoardCopy(board);
            makeMove(tempBoard, move);

            int value = minimax(tempBoard, depth - 1, alpha, beta, false, aiColor, startTime, timeLimit);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }

            alpha = Math.max(alpha, value);
            if (beta <= alpha) break;
        }

        return bestMove;
    }

    private static int minimax(ChessBoard board, int depth, int alpha, int beta, boolean maximizing,
                               int aiColor, long startTime, long timeLimit) {
        if (System.currentTimeMillis() - startTime > timeLimit) {
            return evaluateBoard(board, aiColor);
        }

        if (depth == 0) {
            return quiescenceSearch(board, alpha, beta, maximizing, aiColor, 3);
        }

        int currentPlayer = maximizing ? aiColor : (aiColor == ChessPiece.WHITE ? ChessPiece.BLACK : ChessPiece.WHITE);

        // Check terminal positions first
        if (GameLogic.isInCheckmate(board, currentPlayer)) {
            // If maximizing player is in checkmate, return very negative value
            // If minimizing player is in checkmate, return very positive value
            return maximizing ? -INFINITY + (MAX_DEPTH - depth) : INFINITY - (MAX_DEPTH - depth);
        }

        if (GameLogic.isInStalemate(board, currentPlayer)) {
            return 0; // Draw
        }

        List<Move> moves = generateAllMoves(board, currentPlayer);
        if (moves.isEmpty()) {
            // No legal moves available - this should be checkmate or stalemate
            return maximizing ? -INFINITY + (MAX_DEPTH - depth) : INFINITY - (MAX_DEPTH - depth);
        }

        // Sort moves for better pruning
        moves.sort((m1, m2) -> Integer.compare(getMoveScore(m2), getMoveScore(m1)));

        if (maximizing) {
            int maxEval = -INFINITY;
            for (Move move : moves) {
                ChessBoard tempBoard = createBoardCopy(board);
                makeMove(tempBoard, move);

                int eval = minimax(tempBoard, depth - 1, alpha, beta, false, aiColor, startTime, timeLimit);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Alpha-beta pruning
            }
            return maxEval;
        } else {
            int minEval = INFINITY;
            for (Move move : moves) {
                ChessBoard tempBoard = createBoardCopy(board);
                makeMove(tempBoard, move);

                int eval = minimax(tempBoard, depth - 1, alpha, beta, true, aiColor, startTime, timeLimit);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Alpha-beta pruning
            }
            return minEval;
        }
    }

    private static ChessBoard createBoardCopy(ChessBoard original) {
        ChessBoard copy = new ChessBoard();
        copy.clearBoard();

        // Copy all pieces with their exact state
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

    private static ChessPiece copyPiece(ChessPiece original) {
        ChessPiece copy;
        switch (original.getType()) {
            case PAWN:
                copy = new Pawn(original.getRow(), original.getCol(), original.getColor());
                // Copy pawn-specific state if needed
                break;
            case ROOK:
                copy = new Rook(original.getRow(), original.getCol(), original.getColor());
                // Preserve moved state for castling rights
                if (original instanceof Rook) {
                    Rook originalRook = (Rook) original;
                    Rook copyRook = (Rook) copy;
                    if (originalRook.hasMoved()) {
                        copyRook.setHasMoved(true);
                    }
                }
                break;
            case KNIGHT:
                copy = new Knight(original.getRow(), original.getCol(), original.getColor());
                break;
            case BISHOP:
                copy = new Bishop(original.getRow(), original.getCol(), original.getColor());
                break;
            case QUEEN:
                copy = new Queen(original.getRow(), original.getCol(), original.getColor());
                break;
            case KING:
                copy = new King(original.getRow(), original.getCol(), original.getColor());
                // Preserve moved state for castling rights
                if (original instanceof King) {
                    King originalKing = (King) original;
                    King copyKing = (King) copy;
                    if (originalKing.hasMoved()) {
                        copyKing.setHasMoved(true);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown piece type: " + original.getType());
        }
        return copy;
    }

    private static int quiescenceSearch(ChessBoard board, int alpha, int beta, boolean maximizing, int aiColor, int depth) {
        int standPat = evaluateBoard(board, aiColor);

        if (depth == 0) return standPat;

        if (maximizing) {
            if (standPat >= beta) return beta;
            alpha = Math.max(alpha, standPat);

            List<Move> captures = generateCaptureMoves(board, aiColor);
            // Sort captures by value
            captures.sort((m1, m2) -> Integer.compare(getMoveScore(m2), getMoveScore(m1)));

            for (Move move : captures) {
                ChessBoard tempBoard = createBoardCopy(board);
                makeMove(tempBoard, move);
                int score = quiescenceSearch(tempBoard, alpha, beta, false, aiColor, depth - 1);

                if (score >= beta) return beta;
                alpha = Math.max(alpha, score);
            }
            return alpha;
        } else {
            if (standPat <= alpha) return alpha;
            beta = Math.min(beta, standPat);

            int opponent = aiColor == ChessPiece.WHITE ? ChessPiece.BLACK : ChessPiece.WHITE;
            List<Move> captures = generateCaptureMoves(board, opponent);
            // Sort captures by value
            captures.sort((m1, m2) -> Integer.compare(getMoveScore(m2), getMoveScore(m1)));

            for (Move move : captures) {
                ChessBoard tempBoard = createBoardCopy(board);
                makeMove(tempBoard, move);
                int score = quiescenceSearch(tempBoard, alpha, beta, true, aiColor, depth - 1);

                if (score <= alpha) return alpha;
                beta = Math.min(beta, score);
            }
            return beta;
        }
    }

    private static List<Move> generateAllMoves(ChessBoard board, int color) {
        List<Move> moves = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.pieceAt(row, col);
                if (piece != null && piece.getColor() == color) {
                    // Generate moves based on piece type for better efficiency
                    List<Move> pieceMoves = generateMovesForPiece(board, piece);
                    moves.addAll(pieceMoves);
                }
            }
        }

        return moves;
    }

    private static List<Move> generateMovesForPiece(ChessBoard board, ChessPiece piece) {
        List<Move> moves = new ArrayList<>();
        int row = piece.getRow();
        int col = piece.getCol();

        // Generate moves based on piece type
        switch (piece.getType()) {
            case PAWN:
                generatePawnMoves(board, piece, moves);
                break;
            case ROOK:
                generateSlidingMoves(board, piece, moves, new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}});
                break;
            case BISHOP:
                generateSlidingMoves(board, piece, moves, new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}});
                break;
            case QUEEN:
                generateSlidingMoves(board, piece, moves, new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}});
                break;
            case KNIGHT:
                generateKnightMoves(board, piece, moves);
                break;
            case KING:
                generateKingMoves(board, piece, moves);
                break;
        }

        return moves;
    }

    private static void generatePawnMoves(ChessBoard board, ChessPiece pawn, List<Move> moves) {
        int row = pawn.getRow();
        int col = pawn.getCol();
        int direction = pawn.getColor() == ChessPiece.WHITE ? -1 : 1;

        // Forward moves
        int newRow = row + direction;
        if (newRow >= 0 && newRow < 8 && board.pieceAt(newRow, col) == null) {
            if (GameLogic.canMakeMove(board, pawn, newRow, col)) {
                moves.add(new Move(pawn, row, col, newRow, col, null));
            }

            // Double move from starting position
            if ((pawn.getColor() == ChessPiece.WHITE && row == 6) ||
                    (pawn.getColor() == ChessPiece.BLACK && row == 1)) {
                newRow = row + 2 * direction;
                if (newRow >= 0 && newRow < 8 && board.pieceAt(newRow, col) == null) {
                    if (GameLogic.canMakeMove(board, pawn, newRow, col)) {
                        moves.add(new Move(pawn, row, col, newRow, col, null));
                    }
                }
            }
        }

        // Captures
        for (int colOffset : new int[]{-1, 1}) {
            int newCol = col + colOffset;
            newRow = row + direction;
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                if (GameLogic.canMakeMove(board, pawn, newRow, newCol)) {
                    ChessPiece captured = board.pieceAt(newRow, newCol);
                    moves.add(new Move(pawn, row, col, newRow, newCol, captured));
                }
            }
        }
    }

    private static void generateSlidingMoves(ChessBoard board, ChessPiece piece, List<Move> moves, int[][] directions) {
        int row = piece.getRow();
        int col = piece.getCol();

        for (int[] dir : directions) {
            for (int dist = 1; dist < 8; dist++) {
                int newRow = row + dir[0] * dist;
                int newCol = col + dir[1] * dist;

                if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) break;

                if (GameLogic.canMakeMove(board, piece, newRow, newCol)) {
                    ChessPiece captured = board.pieceAt(newRow, newCol);
                    moves.add(new Move(piece, row, col, newRow, newCol, captured));

                    // Stop if we captured something
                    if (captured != null) break;
                } else {
                    // Can't move here, stop sliding in this direction
                    break;
                }
            }
        }
    }

    private static void generateKnightMoves(ChessBoard board, ChessPiece knight, List<Move> moves) {
        int row = knight.getRow();
        int col = knight.getCol();
        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};

        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];

            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                if (GameLogic.canMakeMove(board, knight, newRow, newCol)) {
                    ChessPiece captured = board.pieceAt(newRow, newCol);
                    moves.add(new Move(knight, row, col, newRow, newCol, captured));
                }
            }
        }
    }

    private static void generateKingMoves(ChessBoard board, ChessPiece king, List<Move> moves) {
        int row = king.getRow();
        int col = king.getCol();
        int[][] kingMoves = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] move : kingMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];

            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                if (GameLogic.canMakeMove(board, king, newRow, newCol)) {
                    ChessPiece captured = board.pieceAt(newRow, newCol);
                    moves.add(new Move(king, row, col, newRow, newCol, captured));
                }
            }
        }

        // Castling moves (if GameLogic supports it)
        if (king instanceof King && !((King) king).hasMoved()) {
            // Kingside castling
            if (GameLogic.canMakeMove(board, king, row, col + 2)) {
                moves.add(new Move(king, row, col, row, col + 2, null));
            }
            // Queenside castling
            if (GameLogic.canMakeMove(board, king, row, col - 2)) {
                moves.add(new Move(king, row, col, row, col - 2, null));
            }
        }
    }

    private static List<Move> generateCaptureMoves(ChessBoard board, int color) {
        List<Move> captures = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.pieceAt(row, col);
                if (piece != null && piece.getColor() == color) {
                    List<Move> pieceMoves = generateMovesForPiece(board, piece);
                    for (Move move : pieceMoves) {
                        if (move.capturedPiece != null || move.isEnPassant) {
                            captures.add(move);
                        }
                    }
                }
            }
        }

        return captures;
    }

    private static int getMoveScore(Move move) {
        int score = 0;

        // Prioritize captures using MVV-LVA (Most Valuable Victim - Least Valuable Attacker)
        if (move.capturedPiece != null) {
            int victimValue = PIECE_VALUES[move.capturedPiece.getType().getCode()];
            int attackerValue = PIECE_VALUES[move.piece.getType().getCode()];
            score += victimValue - attackerValue / 10;
        }

        // Prioritize promotions
        if (move.isPromotion) {
            score += 800;
        }

        // Bonus for castling (king safety)
        if (move.isCastling) {
            score += 50;
        }

        // Small positional bonus for moving to center
        int centerDistance = (int) (Math.abs(3.5 - move.toRow) + Math.abs(3.5 - move.toCol));
        score += (7 - (int) centerDistance);

        return score;
    }

    private static void makeMove(ChessBoard board, Move move) {
        // Remove piece from old position
        board.removePiece(move.fromRow, move.fromCol);

        // Handle special moves
        if (move.isCastling) {
            performCastling(board, (King) move.piece, move.toRow, move.toCol);
        } else if (move.isEnPassant) {
            // Move the pawn
            move.piece.moveTo(move.toRow, move.toCol);
            board.addPiece(move.piece);
            // Remove captured pawn
            int capturedPawnRow = move.piece.getColor() == ChessPiece.WHITE ? move.toRow + 1 : move.toRow - 1;
            board.removePiece(capturedPawnRow, move.toCol);
        } else {
            // Regular move - remove captured piece if any, move piece
            if (move.capturedPiece != null) {
                board.removePiece(move.toRow, move.toCol);
            }
            move.piece.moveTo(move.toRow, move.toCol);
            board.addPiece(move.piece);

            // Handle pawn promotion
            if (move.isPromotion) {
                // Replace pawn with queen (most common promotion)
                board.removePiece(move.toRow, move.toCol);
                Queen promotedQueen = new Queen(move.toRow, move.toCol, move.piece.getColor());
                board.addPiece(promotedQueen);
            }
        }
    }

    private static void performCastling(ChessBoard board, King king, int newRow, int newCol) {
        boolean kingSide = newCol > king.getCol();
        int rookOldCol = kingSide ? 7 : 0;
        int rookNewCol = kingSide ? 5 : 3;

        // Move king
        king.moveTo(newRow, newCol);
        board.addPiece(king);

        // Move rook
        ChessPiece rook = board.pieceAt(newRow, rookOldCol);
        if (rook != null) {
            board.removePiece(newRow, rookOldCol);
            rook.moveTo(newRow, rookNewCol);
            board.addPiece(rook);
        }
    }

    private static int evaluateBoard(ChessBoard board, int aiColor) {
        int materialBalance = 0;
        int positionalBalance = 0;
        int aiPieces = 0;
        int opponentPieces = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.pieceAt(row, col);
                if (piece != null) {
                    int pieceValue = PIECE_VALUES[piece.getType().getCode()];
                    int positionalValue = getPositionalValue(piece, row, col);

                    if (piece.getColor() == aiColor) {
                        materialBalance += pieceValue;
                        positionalBalance += positionalValue;
                        aiPieces++;
                    } else {
                        materialBalance -= pieceValue;
                        positionalBalance -= positionalValue;
                        opponentPieces++;
                    }
                }
            }
        }
        int score = materialBalance + positionalBalance / 10;

        // Mobility evaluation - prefer having more legal moves
        int aiMobility = generateAllMoves(board, aiColor).size();
        int opponentColor = aiColor == ChessPiece.WHITE ? ChessPiece.BLACK : ChessPiece.WHITE;
        int opponentMobility = generateAllMoves(board, opponentColor).size();
        score += (aiMobility - opponentMobility) * 5; // Reduced weight to prevent mobility obsession

        // King safety evaluation
        if (GameLogic.isInCheck(board, opponentColor)) {
            score += 50; // Bonus for putting opponent in check
        }
        if (GameLogic.isInCheck(board, aiColor)) {
            score -= 50; // Penalty for being in check
        }

        // Endgame evaluation - encourage king activity when few pieces remain
        int totalPieces = aiPieces + opponentPieces;
        if (totalPieces < 10) { // Endgame
            // Find kings and evaluate their activity
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    ChessPiece piece = board.pieceAt(row, col);
                    if (piece instanceof King) {
                        int kingActivity = (int) (Math.abs(3.5 - row) + Math.abs(3.5 - col));
                        if (piece.getColor() == aiColor) {
                            score -= (int) (kingActivity * 10); // Encourage AI king to be active
                        } else {
                            score += (int) (kingActivity * 10); // Discourage opponent king activity
                        }
                    }
                }
            }
        }
        return score;
    }

    private static int getPositionalValue(ChessPiece piece, int row, int col) {
        int adjustedRow = piece.getColor() == ChessPiece.WHITE ? 7 - row : row;
        return switch (piece.getType()) {
            case PAWN -> PAWN_TABLE[adjustedRow][col];
            case KNIGHT -> KNIGHT_TABLE[adjustedRow][col];
            case BISHOP -> BISHOP_TABLE[adjustedRow][col];
            case KING -> KING_TABLE[adjustedRow][col];
            case ROOK -> 0; // Rooks are flexible, no specific table needed
            case QUEEN -> 0; // Queens are very flexible
        };
    }
}