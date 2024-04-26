package controller;

import exceptions.AuthenticationException;
import exceptions.NullValueException;
import models.Auth;
import services.UserService;
import utils.DatabaseManager;
import utils.PasswordDecryption;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        DatabaseManager databaseManager = DatabaseManager.getInstance(config);
        PasswordDecryption passwordDecryption;
        try {
            passwordDecryption = new PasswordDecryption(config);
        } catch (Exception e) {
            throw new ServletException("Error initializing PasswordDecryption", e);
        }
        userService = new UserService(databaseManager.getConnection(), passwordDecryption);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Auth auth = userService.login(username, password);

            if (auth != null && auth.isAuthenticated()) {
                // Authentication succeeded
                HttpSession session = request.getSession();
                session.setAttribute("user", auth.getUser());
            
                response.sendRedirect(request.getContextPath() +"/CaptchaServlet");

            }
        } catch (NullValueException e) {
            response.sendRedirect(request.getContextPath() + "/emptyLoginCredentials.jsp?error=" + e.getMessage());
        } catch (AuthenticationException e) {
            // Handle the AuthenticationException
            if (e.getMessage().equals("UsernameNotFound")) {
                // Username not in DB and password is blank
                response.sendRedirect(request.getContextPath() + "/usernameNotFound.jsp?error=User does not exist");
            } else if (e.getMessage().equals("IncorrectPassword")) {
                // Username is correct but password is incorrect
                response.sendRedirect(request.getContextPath() + "/incorrectPassword.jsp?error=The password you entered is invalid");
            } else if (e.getMessage().equals("InvalidCredentials")) {
                // Both username and password are incorrect, and both are not blank
                response.sendRedirect(request.getContextPath() + "/invalidCredentials.jsp?error=The username and password entered are invalid");
            }
        }
    }
}
