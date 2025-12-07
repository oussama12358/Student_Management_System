package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {

    // Loading database settings from the config.properties file
    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();

        // 1) Try to load from classpath (allows placing config.properties inside resources)
        try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
                return props;
            }
        }

        // 2) Fallback to loading from working directory (project root)
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        }

        return props;
    }

    // Establishing a connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            Properties props = loadProperties();
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties connectionProps = new Properties();
            connectionProps.setProperty("user", user);
            connectionProps.setProperty("password", password);
            connectionProps.setProperty("useSSL", "false");
            connectionProps.setProperty("serverTimezone", "UTC");
            connectionProps.setProperty("allowPublicKeyRetrieval", "true");

            return DriverManager.getConnection(url, connectionProps);

        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        } catch (IOException e) {
            throw new SQLException("Could not load config.properties", e);
        }
    }

    // Database and table configuration
    public static void initializeDatabase() {
        try {
            Properties props = loadProperties();
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            String createDB = "CREATE DATABASE IF NOT EXISTS student_db";
            String useDB = "USE student_db";
            String createTable = """
                CREATE TABLE IF NOT EXISTS students (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    major VARCHAR(50) NOT NULL,
                    gpa DOUBLE NOT NULL,
                    enrollment_date DATE NOT NULL
                )
            """;

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                    user, password);
                 Statement stmt = conn.createStatement()) {

                stmt.execute(createDB);
                stmt.execute(useDB);
                stmt.execute(createTable);
                System.out.println("Database initialized successfully!");
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Could not load config.properties: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
