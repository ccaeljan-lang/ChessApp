package ph.edu.dlsu.lbycpob.chessapp.utils;

import javafx.scene.image.Image;
import ph.edu.dlsu.lbycpob.chessapp.model.pieces.ChessPiece;
import ph.edu.dlsu.lbycpob.chessapp.model.pieces.PieceType;

public class ImageLoader {

    private static boolean success = false;

    public static Image[][] loadChessPieceImages() {
        Image[][] pieceImages = new Image[2][6]; // [color][piece_type]

        try {
            // Load white pieces from resources
            pieceImages[ChessPiece.WHITE][PieceType.PAWN.getCode()] = new Image(ImageLoader.class.getResourceAsStream("/images/w_pawn.png"));
            pieceImages[ChessPiece.WHITE][PieceType.ROOK.getCode()] = new Image(ImageLoader.class.getResourceAsStream("/images/w_rook.png"));
            pieceImages[ChessPiece.WHITE][PieceType.KNIGHT.getCode()] = new Image(ImageLoader.class.getResourceAsStream("/images/w_knight.png"));
            pieceImages[ChessPiece.WHITE][PieceType.BISHOP.getCode()] = new Image(ImageLoader.class.getResourceAsStream("/images/w_bishop.png"));
            pieceImages[ChessPiece.WHITE][PieceType.QUEEN.getCode()] = new Image(ImageLoader.class.getResourceAsStream("/images/w_queen.png"));
            pieceImages[ChessPiece.WHITE][PieceType.KING.getCode()] = new Image(ImageLoader.class.getResourceAsStream("/images/w_king.png"));

            // Load black pieces from resources
            pieceImages[ChessPiece.BLACK][PieceType.PAWN.getCode()] = new Image(ImageLoader.class.getResourceAsStream("/images/b_pawn.png"));
            pieceImages[ChessPiece.BLACK][PieceType.ROOK.getCode()] = new Image(ImageLoader.class.getResourceAsStream("/images/b_rook.png"));
            pieceImages[ChessPiece.BLACK][PieceType.KNIGHT.getCode()] = new Image(ImageLoader.class.getResourceAsStream("/images/b_knight.png"));
            pieceImages[ChessPiece.BLACK][PieceType.BISHOP.getCode()] = new Image(ImageLoader.class.getResourceAsStream("/images/b_bishop.png"));
            pieceImages[ChessPiece.BLACK][PieceType.QUEEN.getCode()] = new Image(ImageLoader.class.getResourceAsStream("/images/b_queen.png"));
            pieceImages[ChessPiece.BLACK][PieceType.KING.getCode()] = new Image(ImageLoader.class.getResourceAsStream("/images/b_king.png"));

            success = true;
            System.out.println("Chess images are successfully loaded.");
        } catch (Exception e) {
            System.err.println("Error loading chess piece images from resources: " + e.getMessage());
            success = false;
        }
        return pieceImages;
    }

    public static boolean isSuccess() {
        return success;
    }

    private ImageLoader(){
        // Do not instantiate
    }

}
