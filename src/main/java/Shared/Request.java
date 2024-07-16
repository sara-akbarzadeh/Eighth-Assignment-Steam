package Shared;

import Client.User;
import org.json.JSONObject;
import java.nio.file.Paths;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Request implements Serializable {
    static Scanner in = new Scanner(System.in);
    private String command;
    private User user = null;
    private JSONObject json;

    public Request(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public static JSONObject showMenu() throws IOException {
        System.out.println("Enter command :\n" + "1)Login\n" + "2)SignUp");
        int command = in.nextInt();
        switch (command) {
            case 1://Login
                String username, password;
                System.out.println("Username : ");
                username = in.next();
                System.out.println("Password : ");
                password = in.next();
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("Command", "Login");
                jsonobj.put("username", username);
                jsonobj.put("password", password);
                return jsonobj;

            case 2://SignUp
                System.out.println("Username : ");
                username = in.next();
                System.out.println("Password : ");
                password = in.next();
                Date date;
                while (true) {
                    System.out.println("Birth Date : ");
                    String dateString = in.next();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        date = dateFormat.parse(dateString);
                        break;
                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please enter the date in yyyy-MM-dd format.");
                    }
                }
                jsonobj = new JSONObject();
                jsonobj.put("Command", "SignUp");
                jsonobj.put("username", username);
                jsonobj.put("password", password);
                jsonobj.put("Birthday", date);
                return jsonobj;
        }
        return null;
    }

    public static JSONObject showUserPage(String json, int command) throws IOException, SQLException {
        DataBase DB = new DataBase();
        ResultSet resultSet;
        switch (command) {
            case 1://List of available games
                resultSet = (DB.query("select * from \"Steam\".\"Games\""));
                while (resultSet.next()) {
                    toString(resultSet);
                }
                break;

            case 2://info about a specific game
                System.out.println("Enter title of game :");
                in.nextLine();
                String title = in.nextLine();
                resultSet = (DB.query("select * from \"Steam\".\"Games\""));
                while (resultSet.next()) {
                    if (resultSet.getString("Title").equals(title)) {
                        toString(resultSet);
                    }
                }
                break;
            case 3://Download a game
                downloadFile(json);
                //TODO

        }
        JSONObject jObj = new JSONObject(json);
        return jObj;
    }

    private static void downloadFile(String json) throws SQLException {
        System.out.println("Enter title of game :");
        in.nextLine();
        String title = in.nextLine();
        DataBase DB = new DataBase();
        ResultSet resultSet;
        resultSet = (DB.query("select * from \"Steam\".\"Games\""));
        while (resultSet.next()) {
            if (resultSet.getString("Title").equals(title)) {
                String sourcePath = resultSet.getString("File_path");
                String destinationPath = "D:\\Uni\\Desktopimage.jpg";
                try {
                    Path source = Paths.get(sourcePath);
                    Path destination = Paths.get(destinationPath);

                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Image downloaded successfully.");
                    Response.Downloaded(json, resultSet.getString("ID"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void toString(ResultSet resultSet) throws SQLException {
        System.out.println(
                "Title: " + resultSet.getString("Title") + "\nDeveloper: " + resultSet.getString("Developer") +
                        "\nGenre: " + resultSet.getString("Genre") + "\nPrice: " + resultSet.getDouble("Price") +
                        "\nReviews: " + resultSet.getInt("Reviews") +
                        "\n____________________________________"
        );

    }
}
