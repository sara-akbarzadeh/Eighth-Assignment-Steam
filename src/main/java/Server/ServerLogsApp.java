package Server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerLogsApp extends Application {
    private TextArea logTextArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Server Logs");
        logTextArea = new TextArea();
        logTextArea.setEditable(false);
        VBox root = new VBox(logTextArea);
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void appendLog(String log) {
        logTextArea.appendText(log + "\n");
    }
}
