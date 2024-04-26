package services;

import exceptions.AuthenticationException;
import exceptions.NullValueException;
import models.Auth;
import models.User;
import utils.PasswordDecryption;

import java.sql.*;

import javax.servlet.ServletConfig;

public class UserService {

    private final Connection connection;
    private PasswordDecryption passwordDecryption;

   public UserService(Connection connection, PasswordDecryption passwordDecryption) {
    this.connection = connection;
    this.passwordDecryption = passwordDecryption;
}


    /**
     * Authenticates a user
     *
     * @param email Email of the user that will be logged in
     * @param password Password of the user that will be logged in
     * @return Auth object (containing both User object and isAuthenticated
     * boolean)
     */
    public Auth login(String email, String password) throws NullValueException, AuthenticationException {
        boolean isBlankCredentials = (email == null || email.isEmpty()) && (password == null || password.isEmpty());

        if (isBlankCredentials) {
            throw new NullValueException("Username and password must not be empty.");
        }

        User userResult = null;
        boolean isAuthenticated = false;

        String authenticateString = "SELECT * FROM USER_INFO WHERE email = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(authenticateString);
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password").trim();
                String decryptedPassword;
                try {
                    decryptedPassword = passwordDecryption.decrypt(storedPassword);
                } catch (Exception e) {
                    throw new RuntimeException("Error decrypting password", e);
                }

                if (password.equals(decryptedPassword)) {
                    userResult = new User(
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getString("user_role")
                    );

                    isAuthenticated = true;
                } else {
                    // error_2.jsp (if the username is correct but incorrect password)
                    throw new AuthenticationException("IncorrectPassword");
                }
            } else {
                if ((password == null || password.isEmpty())) {
                    // error_1.jsp (if the username is not in the DB, and password is blank)
                    throw new AuthenticationException("UsernameNotFound");
                } else {
                    // error_3.jsp (if the username and password are both incorrect, both are not blank)
                    throw new AuthenticationException("InvalidCredentials");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error detail: " + e.getMessage());
        }

        return new Auth(userResult, isAuthenticated);
    }
}
