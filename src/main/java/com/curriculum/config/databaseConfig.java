package com.curriculum.config;

public class databaseConfig {
    public static String URL;
    public static String USER;
    public static String PASSWORD;

    static {
        URL = System.getenv("DATABASE_URL");
        USER = System.getenv("DB_USER");
        PASSWORD = System.getenv("DB_PASSWORD");
    }
}

