package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostegresDB implements IDB {

    private static PostegresDB instance;

    private final String URL =
            "jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:5432/postgres?sslmode=require";
    private final String USER = "postgres.gbnowgnkrkklcunnabvv";
    private final String PASSWORD;


    private PostegresDB() {
        PASSWORD = loadPassword();
    }


    public static PostegresDB getInstance() {
        if (instance == null) {
            synchronized (PostegresDB.class) {
                if (instance == null) {
                    instance = new PostegresDB();
                }
            }
        }
        return instance;
    }

    private String loadPassword() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            props.load(input);
            String value = props.getProperty("DB_PASSWORD");
            if (value == null || value.isBlank()) {
                throw new RuntimeException("DB_PASSWORD is not set in config.properties");
            }
            return value;
        } catch (IOException e) {
            throw new RuntimeException("Cannot load DB_PASSWORD from config.properties", e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
