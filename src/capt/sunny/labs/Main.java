package capt.sunny.labs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Main {

    public static void main(String[] args) throws SQLException {
        Connection connection = null;
        String url = "jdbc:postgresql://127.0.0.1:1338/studs";
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException | NullPointerException e) {
            System.out.println(
                    "Could not find org.postgresql.Driver. " +
                    "The server can not work..."
            );
        }
        String login = "не смотри";
        String password  = "сюда";

        connection = DriverManager.getConnection(url, login, password);
        System.out.println("coonnected");
        connection.close();
    }

}

