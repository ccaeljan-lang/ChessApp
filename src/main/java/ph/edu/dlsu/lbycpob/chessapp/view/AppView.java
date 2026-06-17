package ph.edu.dlsu.lbycpob.chessapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ph.edu.dlsu.lbycpob.chessapp.model.ChessBoard;

public class AppView {

    private final ChessBoardView board;
    private final  BorderPane root;
    private final  TextArea moveHistory;
    private final  Text statusText;
    private final Button replayButton;

    public AppView() {
        this.board = new ChessBoardView();

        root = new BorderPane();
        root.getStyleClass().add("chess-root");

        // Chess board in the center
        root.setCenter(board);

        // Setup status
        statusText = new Text("White to move");
        statusText.getStyleClass().add("status-text");

        // Setup bottom panel
        VBox bottomPanel = setupBottomPanel();

        // Move history
        moveHistory = new TextArea();
        setupMoveHistory();

        // Button panel
        replayButton = new Button("New Game");
        HBox buttonPanel = createButtonPanel();

        // Add to Bottom Panel
        bottomPanel.getChildren().addAll(statusText, moveHistory, buttonPanel);
        root.setBottom(bottomPanel);
    }

    private VBox setupBottomPanel() {
        VBox bottomPanel = new VBox(10);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setAlignment(Pos.CENTER);
        return bottomPanel;
    }

    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(10);
        replayButton.getStyleClass().add("button");
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.getChildren().add(replayButton);
        return buttonPanel;
    }

    public void setupMoveHistory() {
        moveHistory.getStyleClass().add("move-history");
        moveHistory.setEditable(false);
        moveHistory.setWrapText(true);
        moveHistory.setPrefRowCount(4);
        moveHistory.setPromptText("Move history will appear here...");
    }

    public Button getReplayButton() {
        return replayButton;
    }

    public ChessBoardView getBoard() {
        return board;
    }

    public TextArea getMoveHistory() {
        return moveHistory;
    }

    public Text getStatusText() {
        return statusText;
    }

    public int[] getLocation(double x, double y) {
        return board.getLocation(x, y);
    }

    public void updateBoard(ChessBoard model) {
        board.updateBoard(model);
    }

    public BorderPane getRoot() { return root; }
}