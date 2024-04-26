package utils;

import java.sql.*;

import javax.servlet.ServletConfig;

public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;
    private PasswordEncryption passwordEncryption;

    private DatabaseManager(ServletConfig config) {
        try {
            Class.forName(config.getInitParameter("jdbcClassName"));
            String username = config.getInitParameter("dbUserName");
            String password = config.getInitParameter("dbPassword");
            String url = config.getInitParameter("jdbcDriverURL")
                    + "://"
                    + config.getInitParameter("dbHostName")
                    + ":"
                    + config.getInitParameter("dbPort")
                    + "/"
                    + config.getInitParameter("databaseName");
            connection = DriverManager.getConnection(url, username, password);

            // Initialize PasswordEncryption
            passwordEncryption = new PasswordEncryption(config);

            populateTable();

            System.out.println("Connection established...");
        } catch (SQLException sqle) {
            System.out.println("SQLException error occurred - " + sqle.getMessage());
        } catch (ClassNotFoundException nfe) {
            System.out.println("ClassNotFoundException error occurred - " + nfe.getMessage());
        } catch (Exception e) {
            System.out.println("Exception error occurred - " + e.getMessage());
        }
    }

    public static DatabaseManager getInstance(ServletConfig config) {
        if (instance == null) {
            instance = new DatabaseManager(config);
        }
        return instance;
    }

    // Auto generates accounts with encrypted passwords 
    private void populateTable() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS rowcount FROM USER_INFO");
            resultSet.next();
            int count = resultSet.getInt("rowcount");
            resultSet.close();

            if (count == 0) {
                // Encrypt the password
                String encryptedPassword;
                try {
                    encryptedPassword = passwordEncryption.encrypt("admin");
                } catch (Exception e) {
                    throw new RuntimeException("Error encrypting password", e);
                }

                for (int i = 1; i <= 50; i++) {
                    String query = "INSERT INTO USER_INFO (email, password, user_role) VALUES ('admin" + i + "@admin.com', '" + encryptedPassword + "', 'admin')";
                    statement.executeUpdate(query);
                }

                // Encrypt the guest password
                String encryptedGuestPassword;
                try {
                    encryptedGuestPassword = passwordEncryption.encrypt("guest");
                } catch (Exception e) {
                    throw new RuntimeException("Error encrypting password", e);
                }

                for (int i = 1; i <= 50; i++) {
                    String query = "INSERT INTO USER_INFO (email, password, user_role) VALUES ('guest" + i + "@guest.com', '" + encryptedGuestPassword + "', 'guest')";
                    statement.executeUpdate(query);
                }

                System.out.println("Data inserted successfully...");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

}
