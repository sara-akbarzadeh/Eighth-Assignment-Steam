package Client;

import Shared.Request;
import java.io.*;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.sql.SQLException;
import java.util.Scanner;
import org.json.JSONObject;


public class ClientMain {
    static boolean loggedIn=false;
    static User user=null;
    static Request request;
    static Scanner in = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        try {
            Socket socket = new Socket("127.0.0.1", 2345);
            System.out.println("Connected to server!");
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(input));
            PrintWriter out = new PrintWriter(output, true);
            out.println(Request.showMenu());
            String response = read.readLine();
            while (response != null) {
                JSONObject json=new JSONObject(response);
                switch (json.getString("Command")) {
                    case "Login":
                    case "SignUp":
                        String status = json.getString("status");
                        if (status.equals("true")) {
                            System.out.println("Enter command:\n" + "1)List of available games\n" + "2)info about a specific game\n" + "3)Download a game\n" + "4)Logout");
                            int command = in.nextInt();
                            if (command == 4) { // logout
                                return;
                            }
                            out.println(Request.showUserPage(String.valueOf(json), command));
                        } else if (status.equals("false")) {
                            System.out.println("Try again!");
                            out.println(Request.showMenu());
                        }
                        break;
                    // Other cases
                }
                response = read.readLine();
            }
        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}