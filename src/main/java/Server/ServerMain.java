package Server;

import Shared.Response;
import org.json.JSONObject;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerMain {
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerMain server = new ServerMain(2345);
        server.start();
    }

    public ServerMain(int portNumber) throws IOException {
        this.serverSocket = new ServerSocket(portNumber);
    }

    public void start() {
        System.out.println("Server started.");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                handler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader read;
        private PrintWriter out;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }

        public void run() {
            String inputLine;
            try {
                while ((inputLine = read.readLine()) != null) {
                    System.out.println("Received message from " + socket.getRemoteSocketAddress() + ": " + inputLine);
                    JSONObject json = new JSONObject(inputLine);
                    String command = json.getString("Command");

                    switch (command) {
                        case "Login":
                            out.println(Response.Login(json));
                            break;
                        case "SignUp":
                            out.println(Response.SignUp(json));
                            break;
                        case "List of available games":
                            // Handle the command here
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    clients.remove(this);
                    System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void broadcast(String message) {
            for (ClientHandler client : clients) {
                client.out.println(message);
            }
        }
    }
}
