package ph.edu.dlsu.lbycpob.chessapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ph.edu.dlsu.lbycpob.chessapp.controller.ChessController;
import ph.edu.dlsu.lbycpob.chessapp.utils.BackgroundMusicManager;

// ChessApp.java
public class ChessApp extends Application {

    private BackgroundMusicManager musicManager;

    @Override
    public void start(Stage primaryStage) {
        ChessController controller = new ChessController();
        Scene scene = new Scene(controller.getView(), 840, 880);
        scene.getStylesheets().add(getClass().getResource("/styles/chess.css").toExternalForm());

        // Setup background sound: Initialize music manager
        musicManager = BackgroundMusicManager.getInstance();
        // Load background music (place your music files in src/main/resources/audio/)
        musicManager.loadMusic("puzzle_theme", "/audio/background.mp3");
        // Start playing default music
        musicManager.playMusic("puzzle_theme");

        primaryStage.setTitle("LBYCPEI Chess Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(e -> {
            musicManager.dispose(); // Cleanup resources
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}