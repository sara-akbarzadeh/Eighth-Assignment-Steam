package Shared;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
public class DataBase {
    static Statement statement;
    static private Connection connection;
    public DataBase() {
        try {
            Class.forName("org.postgresql.database");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/MyDataBase","root","1382");
            statement = connection.createStatement();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Statement getStatement() {
        return statement;
    }

    public static void setStatement(Statement statement) {
        DataBase.statement = statement;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        DataBase.connection = connection;
    }

    public ResultSet query(String sql){
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}