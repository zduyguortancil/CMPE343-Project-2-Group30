package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // CORRECTED: Added parameters for secure BCrypt hash handling and proper encoding.
    private static final String URL =
            "jdbc:mysql://localhost:3306/cmpe343?" + 
            "useSSL=false&" + 
            "allowPublicKeyRetrieval=true&" +
            "defaultAuthScaheme=mysql_native_password&" + // Crucial for compatibility
            "useUnicode=true&" + 
            "characterEncoding=UTF-8&" + // Crucial for reading special hash characters
            "serverTimezone=UTC";
    
    private static final String USER = "root"; 
    private static final String PASSWORD = "1234abcd";

    /**
     * Establishes and returns the database connection.
     */
    public static Connection getConnection() {
        try {
            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL Driver NOT FOUND!");
            e.printStackTrace();
            return null;

        } catch (SQLException e) {
            System.err.println("ERROR: Database connection failed. Check User/Password/URL.");
            e.printStackTrace();
            return null;
        }
    }
}