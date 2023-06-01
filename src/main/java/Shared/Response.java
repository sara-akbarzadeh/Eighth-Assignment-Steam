package Shared;

import org.json.JSONObject;
import java.io.File;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Response implements Serializable {
    private File file;
    private JSONObject jsonObj;
    private String json;
    private String message;

    public Response(String json) {
        this.json = json;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public JSONObject getJsonObj() {
        return jsonObj;
    }

    public void setJsonObj(JSONObject jsonObj) {
        this.jsonObj = jsonObj;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public static JSONObject Login(JSONObject json) throws SQLException {
        String username = json.getString("username");
        String password = json.getString("password");
        DataBase DB = new DataBase();
        ResultSet resultSet = (DB.query("SELECT * FROM \"Steam\".\"accounts\""));
        while (resultSet.next()) {
            if (resultSet.getString("Username").equals(username) && resultSet.getString("Password").equals(password)) {
                json.put("status", "true");
                return json;
            }
        }
        json.put("status", "false");
        return json;
    }

    public static JSONObject SignUp(JSONObject json) throws SQLException {
        String username = json.getString("username");
        String password = json.getString("password");
        DataBase DB = new DataBase();
        ResultSet resultSet = (DB.query("SELECT * FROM \"Steam\".\"accounts\""));
        while (resultSet.next()) {
            if (resultSet.getString("Username").equals(username)) {
                json.put("status", "false");
                return json;
            }
        }
        json.put("status", "true");
        String ID = "2";
        String sql = "INSERT INTO \"Steam\".\"accounts\" VALUES ('" + ID + "','" + username + "', '" +
                password + "','" + json.getString("Birthday") + "')";

        DB.query(sql);
        return json;
    }

    public static void Downloaded(String json, String gameId) {
        JSONObject jObj = new JSONObject(json);
        DataBase DB = new DataBase();
        String sql = "INSERT INTO \"Steam\".\"Downloads\" VALUES ('" +
                jObj.getString("username") + "','" + gameId + "', '" +
                '1' + "')";

        DB.query(sql);
    }
}
