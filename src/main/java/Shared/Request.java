package Shared;

import Client.Menus;
import Client.User;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Request {
    public static String createRequest(JSONObject response, Scanner scan) {
        String type = response.getString("type");

        switch (type) {
            case "lobby menu":
                return createLobbyMenuRequests(scan);
            case "user menu":
                return createUserMenuRequests(scan, new User(response));
            case "sign up":
                return createSignUpRequest(scan);
            case "log in":
                return createLogInRequest(scan);
            case "view game list":
                showGameList(response.getJSONObject("games"));
                break;
        }

        return null;
    }
    private static void showGameList(JSONObject games) {
        List<String> ids = new ArrayList<>(games.keySet());

        for (int i = 0; i < ids.size(); i++) {
            JSONObject game = games.getJSONObject(ids.get(i));
            System.out.println(i + 1 + ". " + game.getString("title") + ", " +
                    game.getString("genre") + ", " + game.getString("reviews"));
        }
    }

    private static String createUserMenuRequests(Scanner scan, User user) {
        System.out.println(user.getUsername());
        System.out.println(Menus.userMenu());
        System.out.println("insert a number");
        int ch = Integer.parseInt(scan.nextLine());

        switch (ch) {
            case 1:
                return viewGameListRequest(user);
            case 3:
                return showLobbyMenuRequest();
        }

        return null;
    }

    private static String viewGameListRequest(User user) {
        JSONObject json = new JSONObject();
        json.put("type", "view games");
        json.put("user", user.userTOJson());
        return json.toString();
    }

    public static String showLobbyMenuRequest() {
        JSONObject json = new JSONObject();
        json.put("type", "lobby menu");
        return json.toString();
    }

    public static String showUserMenuRequest(User user) {
        JSONObject json = new JSONObject();
        json.put("type", "user menu");
        json.put("username", user.getUsername());
        return json.toString();
    }

    public static String createLobbyMenuRequests(Scanner scan) {
        System.out.println(Menus.lobbyMenu());
        System.out.println("insert a number");
        int ch = Integer.parseInt(scan.nextLine());

        switch (ch) {
            case 1:
                return createSignUpRequest(scan);
            case 2:
                return createLogInRequest(scan);
        }

        return null;
    }


    private static String createLogInRequest(Scanner scan) {
        JSONObject json = new JSONObject();
        json.put("type", "log in");

        System.out.println("insert your username");
        String username = scan.nextLine();
        json.put("username", username);

        System.out.println("insert your password");
        String password = scan.nextLine();
        json.put("password", password);

        return json.toString();
    }

    public static String createSignUpRequest(Scanner scan) {
        JSONObject json = new JSONObject();
        json.put("type", "sign up");

        System.out.println("insert your username");
        String username = scan.nextLine();
        json.put("username", username);

        System.out.println("insert your password");
        String password = scan.nextLine();
        json.put("password", password);

        return json.toString();
    }

}
