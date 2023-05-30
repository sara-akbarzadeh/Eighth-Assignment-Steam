package Shared;
import Server.InsertData;
import org.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Response {
    public static String responseCreator(JSONObject request,Statement statement) throws SQLException {
        String type = request.getString("type");

        if (type.equals("lobby menu")){
            return lobbyMenuResponse();
        }

        else if (type.equals("user menu")){
            return userMenuResponse(request);
        }

        else if(type.equals("sign up")){
            return signUpResponseCreator(doesUserExist(request,statement),statement,request);
        }

        else if (type.equals("log in")){
            return logInResponseCreator(doesUserExist(request,statement),statement,request);
        }

        else if (type.equals("view games")){
            return viewGameListResponse(statement, request);
        }

        return null;
    }

    private static String viewGameListResponse(Statement statement, JSONObject request) throws SQLException {
        JSONObject json = new JSONObject();

        json.put("type", "view game list");
        json.put("user",request.getJSONObject("user"));
        ArrayList<String> columns = columnNames(statement);
        System.out.println(columns);

        ResultSet result = statement.executeQuery("SELECT * FROM games");
        result.next();

        JSONObject games = new JSONObject();

        while (!result.isAfterLast()){
            JSONObject details = new JSONObject();
            for (int j=0;j<8;j++){
                String column = columns.get(j);
                details.put(column,result.getString(column));
            }
            System.out.println(details);
            games.put(result.getString("id"),details);
            result.next();
        }
        json.put("games",games);

        System.out.println(json.toString());

        return json.toString();
    }

    private static String logInResponseCreator(String doesUserExist, Statement statement, JSONObject request) throws SQLException {
        JSONObject json = new JSONObject();
        json.put("type", "log in");
        json.put("username",request.getString("username"));

        if (doesUserExist.equals("true")){
            ResultSet result = statement.executeQuery("SELECT * FROM users WHERE username = '" + request.getString("username") + "'");
            result.next();
            if (result.getString("password").equals(request.getString("password"))){
                json.put("status", "true");
            }

            else{
                json.put("status", "false");
                json.put("reason","password is incorrect");
            }
        }

        else{
            json.put("status", "false");
            json.put("reason","no user found with such username");
        }

        return json.toString();
    }

    public static String lobbyMenuResponse(){
        JSONObject json = new JSONObject();

        json.put("type","lobby menu");

        return json.toString();
    }

    public static String userMenuResponse(JSONObject request){
        JSONObject json = new JSONObject();

        json.put("type","user menu");
        json.put("username",request.getString("username"));

        return json.toString();
    }

    public static String signUpResponseCreator(String doesUserExist, Statement statement,JSONObject json) throws SQLException {
        if (doesUserExist.equals("false")){
            InsertData.insertUser(json,statement);
        }

        json.put("type","sign up");

        if (doesUserExist.equals("false")) {
            json.put("status", "true");
        }

        else{
            json.put("status", "false");
        }

        return json.toString();
    }

    public static String doesUserExist(JSONObject json,Statement statement) throws SQLException {
        ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM users WHERE username = '" +
                json.getString("username") + "'");
        result.next();

        if (result.getInt("count") == 0){
            return "false";
        }
        return "true";
    }

    public static ArrayList<String> columnNames(Statement statement) throws SQLException {
        ArrayList<String> columns = new ArrayList<>();

        for (int i=2;i<=9;i++) {
            String sql = "SELECT column_name FROM information_schema.columns\n" +
                    "WHERE table_name = '" + "games" + "' AND ordinal_position = " + i + ";";
            ResultSet result = statement.executeQuery(sql);
            result.next();
            columns.add(result.getString("column_name"));
        }

        return columns;
    }
}