package com.gearrent.util;
import java.sql.Connection; import java.sql.DriverManager; import java.sql.SQLException;
public class DBConnection {
    private static final String URL  = "jdbc:mysql://localhost:3306/gearrent_pro?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root"; // <-- change to your MySQL password
    private static DBConnection instance;
    private DBConnection(){
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e){ throw new RuntimeException(e); }
    }
    public static synchronized DBConnection getInstance(){
        if (instance==null) instance = new DBConnection();
        return instance;
    }
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
