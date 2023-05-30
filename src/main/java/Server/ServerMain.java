package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerMain {
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    private Connection connection;
    private ServerLogsApp logsApp;

    public static void main(String[] args) throws IOException, SQLException {
        int portNumber = 1234;
        ServerMain server = new ServerMain(portNumber);
        server.start();
    }

    public ServerMain(int portNumber) throws IOException, SQLException {
        this.serverSocket = new ServerSocket(portNumber);
        this.connection = connectSQL();
        this.logsApp = new ServerLogsApp();
    }

    public void start() {
        System.out.println("Server started");

        // Launch JavaFX application
        Thread fxThread = new Thread(() -> ServerLogsApp.launch(ServerLogsApp.class));
        fxThread.setDaemon(true);
        fxThread.start();

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getRemoteSocketAddress());
                logsApp.appendLog("New client connected: " + socket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(socket, connection);
                clients.add(handler);
                handler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Connection connectSQL() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/steam";
        String user = "postgres";
        String pass = "1382";
        Connection connection = DriverManager.getConnection(url, user, pass);
        System.out.println("Connected to the mySQL database!");
        logsApp.appendLog("Connected to the mySQL database!");
        return connection;
    }

}
