package Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.rmi.UnknownHostException;

import org.json.JSONObject;

public class ClientApp extends Application {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 2345;
    private Socket clientSocket;
    private BufferedReader read;
    private PrintWriter out;
    private TextArea logTextArea;
    private TextField commandTextField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        logTextArea = new TextArea();
        logTextArea.setEditable(false);
        VBox.setMargin(logTextArea, new Insets(10));

        commandTextField = new TextField();
        commandTextField.setOnAction(e -> sendCommand());

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendCommand());

        VBox inputVBox = new VBox(10, commandTextField, sendButton);
        inputVBox.setPadding(new Insets(10));
        inputVBox.setMaxWidth(200);

        root.setLeft(inputVBox);
        root.setCenter(logTextArea);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Client App");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> disconnectFromServer());
        primaryStage.show();

        connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
                read = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                log("Connected to server: " + SERVER_HOST + ":" + SERVER_PORT);

                // Start listening for server responses
                listenForResponses();
            } catch (UnknownHostException e) {
                log("Server not found: " + e.getMessage());
            } catch (IOException e) {
                log("I/O error: " + e.getMessage());
            }
        }).start();
    }

    private void disconnectFromServer() {
        if (clientSocket != null && !clientSocket.isClosed()) {
            try {
                clientSocket.close();
                log("Disconnected from server.");
            } catch (IOException e) {
                log("Failed to close client socket: " + e.getMessage());
            }
        }
    }

    private void sendCommand() {
        String command = commandTextField.getText();
        out.println(command);
        commandTextField.clear();
        log("Sent command: " + command);
    }

    private void listenForResponses() {
        try {
            String response;
            while ((response = read.readLine()) != null) {
                log("Received response: " + response);

                JSONObject json = new JSONObject(response);
                String command = json.getString("Command");

                switch (command) {
                    case "Login":
                    case "SignUp":
                        String status = json.getString("status");
                        if (status.equals("true")) {
                            log("Enter command:\n1)List of available games\n2)info about a specific game\n3)Download a game\n4)Logout");
                        } else if (status.equals("false")) {
                            log("Try again!");
                        }
                        break;
                    // Other cases
                }
            }
        } catch (IOException e) {
            log("Error reading server response: " + e.getMessage());
        }
    }

    private void log(String message) {
        Platform.runLater(() -> logTextArea.appendText(message + "\n"));
    }
}

