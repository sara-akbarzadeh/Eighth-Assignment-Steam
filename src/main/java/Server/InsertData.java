package Server;

import org.json.JSONObject;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class InsertData {
    public static void insertUser(JSONObject json, Statement statement) throws SQLException {
        System.out.println(json);

        String query = "INSERT INTO users VALUES (null, ?, ?, null)";
        PreparedStatement preparedStatement = statement.getConnection().prepareStatement(query);
        preparedStatement.setString(1, json.getString("username"));
        preparedStatement.setString(2, json.getString("password"));

        preparedStatement.executeUpdate();
    }
}
