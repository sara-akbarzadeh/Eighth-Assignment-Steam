package Client;
import Shared.Request;
import org.json.JSONObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class JavaFXClient extends Application {
    private String hostname = "123.0.6.7";
    private int port = 1234;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Scanner scanner;
    private TextArea responseTextArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Client");

        responseTextArea = new TextArea();
        responseTextArea.setEditable(false);

        Button sendButton = new Button("Send");

        sendButton.setOnAction(e -> {
            String response = responseTextArea.getText();
            JSONObject responseJson = new JSONObject(response);
            String request = Request.createRequest(responseJson, scanner);
            writer.println(request);
        });

        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(10));
        hbox.getChildren().addAll(sendButton);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(responseTextArea);
        borderPane.setBottom(hbox);

        Scene scene = new Scene(borderPane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            socket = new Socket(hostname, port);

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            scanner = new Scanner(System.in);

            new Thread(this::handleServerResponses).start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    private void handleServerResponses() {
        try {
            String response;
            while ((response = reader.readLine()) != null) {
                if (!response.equals("null")) {
                    JSONObject responseJson = new JSONObject(response);
                    String request = Request.createRequest(responseJson, scanner);

                    Platform.runLater(() -> {
                        responseTextArea.appendText(request + "\n");
                    });
                }
            }

        } catch (IOException ex) {
            System.out.println("Error reading server response: " + ex.getMessage());
        }
    }

    @Override
    public void stop() {
        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error closing socket: " + ex.getMessage());
        }
    }
}
