package com.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Michael
 */
public class dataManager {

    public static Connection getSQLiteConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:main/java/com/data/local.db");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        if (getSQLiteConnection() != null) {
            System.out.println("Connected Successfully");
        } else {
            System.out.println("Connection Failed!");
        }
    }
}
