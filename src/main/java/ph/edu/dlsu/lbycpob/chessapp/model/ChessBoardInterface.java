package ph.edu.dlsu.lbycpob.chessapp.model;

import ph.edu.dlsu.lbycpob.chessapp.model.pieces.ChessPiece;

// Interface for board operations
public interface ChessBoardInterface {
    ChessPiece pieceAt(int row, int col);
    void addPiece(ChessPiece piece);
    void removePiece(int row, int col);
}