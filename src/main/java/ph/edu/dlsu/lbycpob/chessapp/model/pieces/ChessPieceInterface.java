package ph.edu.dlsu.lbycpob.chessapp.model.pieces;

import ph.edu.dlsu.lbycpob.chessapp.model.ChessBoard;

// ChessPieceInterface.java: Interface for chess pieces
public interface ChessPieceInterface {
    boolean canMoveTo(int row, int col, ChessBoard board);
    PieceType getType();
    int getColor();
    int getRow();
    int getCol();
    void moveTo(int row, int col);
}
