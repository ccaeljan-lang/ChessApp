package ph.edu.dlsu.lbycpob.chessapp.model.pieces;

// PieceType.java
public enum PieceType {
    PAWN(0), ROOK(1), KNIGHT(2), BISHOP(3), QUEEN(4), KING(5);

    private final int code;

    PieceType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

