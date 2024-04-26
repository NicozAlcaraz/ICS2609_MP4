package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CaptchaServlet extends HttpServlet {

    // Captcha class
    public static class Captcha {

        // Generates a CAPTCHA of given length
        public static String generateCaptcha(int n) {
            // Characters to be included
            String chrs = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

            // Generate n characters from above set and
            // add these characters to captcha.
            String captcha = "";
            while (n-- > 0) {
                int index = (int) (Math.random() * 62);
                captcha += chrs.charAt(index);
            }

            return captcha;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // CaptchaServlet is in charge of verifying the user input to the generated captcha
        String userCaptcha = request.getParameter("userCaptcha").trim();
        HttpSession session = request.getSession();
        String captcha = (String) session.getAttribute("captcha");

        if (captcha.equals(userCaptcha)) {
            response.sendRedirect(request.getContextPath() + "/success.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/error_captcha.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int n = Integer.parseInt(getServletConfig().getInitParameter("captchaLength"));
        String captcha = Captcha.generateCaptcha(n);
        
        HttpSession session = request.getSession();
        session.setAttribute("captcha", captcha);
        
        // Updates the server logs with the generated captcha
        System.out.print("captcha: " +captcha);
        
        // Forward to the JSP where you want to display the CAPTCHA
        request.getRequestDispatcher("/captcha.jsp").forward(request, response);
    }
}
