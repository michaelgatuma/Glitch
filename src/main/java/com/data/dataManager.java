package com.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Michael
 */
public class dataManager {
    private static String path = "jdbc:sqlite:required/local.db";
    //private static String path = "main/java/com/data/local.db";

    public static Connection getSQLiteConnection() {
        //run below block if database path does not exist
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(path);
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
