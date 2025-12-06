package com.curriculum.database;
import com.curriculum.config.databaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectionFactory {
    public static Connection getConnection()throws SQLException {
          return DriverManager.getConnection(
                  databaseConfig.URL,
                  databaseConfig.USER,
                  databaseConfig.PASSWORD
          );
    }
}
