package Server;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import Shared.Response;

public class ClientHandler extends Thread {
    private Socket socket;
    private Connection connection;
    private BufferedReader in;
    private PrintWriter out;
    private Statement statement;
    private static List<ClientHandler> clients = new ArrayList<>();

    public ClientHandler(Socket socket, Connection connection) throws IOException {
        this.socket = socket;
        this.connection = connection;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        try {
            this.statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        broadcast(Response.lobbyMenuResponse(), this);
        String request;
        try {
            while ((request = in.readLine()) != null) {
                if (!request.equals("null")) {
                    System.out.println(request);
                    String response = Response.responseCreator(new JSONObject(request), statement);
                    System.out.println(response);
                    broadcast(response, this);
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                clients.remove(this);
                System.out.println("closed");
                statement.close();
                connection.close();
                System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcast(String message, ClientHandler client) {
        client.out.println(message);
    }
}
