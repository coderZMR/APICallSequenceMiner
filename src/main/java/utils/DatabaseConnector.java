package utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @Author zmr
 * @Date 2020/10/10,12:18 PM
 */
public class DatabaseConnector {

    private Properties prop;
    private Connection conn;


    public DatabaseConnector() {
        prop = new Properties();
        try {
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("mysql_jdbc.properties"));
            Class.forName(prop.getProperty("driver"));
            conn = DriverManager.getConnection(
                    prop.getProperty("url"),
                    prop.getProperty("user"),
                    prop.getProperty("password"));
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConn() {
        return conn;
    }
}
