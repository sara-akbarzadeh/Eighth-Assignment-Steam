package Server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerApp extends Application {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 2345;
    private Socket clientSocket;
    private PrintWriter out;
    private TextArea logTextArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        logTextArea = new TextArea();
        logTextArea.setEditable(false);
        VBox.setMargin(logTextArea, new Insets(10));

        Button startServerButton = new Button("Start Server");
        startServerButton.setOnAction(e -> {
            startServer();
            startServerButton.setDisable(true);
        });

        Button stopServerButton = new Button("Stop Server");
        stopServerButton.setOnAction(e -> {
            stopServer();
            startServerButton.setDisable(false);
        });

        VBox controlsVBox = new VBox(10, startServerButton, stopServerButton);
        controlsVBox.setPadding(new Insets(10));
        controlsVBox.setMaxWidth(200);

        root.setLeft(controlsVBox);
        root.setCenter(logTextArea);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Server App");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> stopServer());
        primaryStage.show();
    }

    private void startServer() {
        new Thread(() -> {
            try {
                clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                log("Connected to server: " + SERVER_HOST + ":" + SERVER_PORT);
            } catch (IOException e) {
                log("Failed to connect to server: " + e.getMessage());
            }
        }).start();
    }

    private void stopServer() {
        if (clientSocket != null && !clientSocket.isClosed()) {
            try {
                clientSocket.close();
                log("Disconnected from server.");
            } catch (IOException e) {
                log("Failed to close client socket: " + e.getMessage());
            }
        }
    }

    private void log(String message) {
        Platform.runLater(() -> logTextArea.appendText(message + "\n"));
    }
}
