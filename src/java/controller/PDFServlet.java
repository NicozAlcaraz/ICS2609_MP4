package controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import utils.DatabaseManager;
import utils.PasswordDecryption;

public class PDFServlet extends HttpServlet {

    private Connection connection;

    @Override
    public void init() throws ServletException {
        // Initialize the database connection
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance(getServletConfig());
            connection = dbManager.getConnection();
        } catch (Exception e) {
            throw new ServletException("Error initializing database connection", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String role = ((models.User) request.getSession().getAttribute("user")).getUserRole();
        String email = ((models.User) request.getSession().getAttribute("user")).getUsername();

        // session checker if session is not destroyed upon access
        if (session == null) {
            System.out.print("session is not available");
            return;
        }

        // checks if role is not fetched or is null
        if (role == null) {
            System.out.print("role is not available");
            return;
        }

        // checks if the database does exist
        if (connection == null) {
            System.out.print("db connection is not available");
            return;
        }

        // prints out the current user type accessing the program upon login
        System.out.print(role);

        // if logged in user is admin then it will call generateAdminReport method
        // to generate a report designated for admins
        if (role.equals("admin")) {
            try {
                generateAdminReport(response, email);
            } catch (DocumentException e) {
                throw new ServletException(e);
            }
            // if logged in user is guest then it will call generateGuestReport method
            // to generate a report designated for guests
        } else if (role.equals("guest")) {
            try {
                generateGuestReport(response, email, getServletConfig());
            } catch (DocumentException e) {
                throw new ServletException(e);
            }
        }
    }

    private void generateAdminReport(HttpServletResponse response, String email) throws DocumentException, IOException {
        Document document = new Document(PageSize.LETTER);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        String Arole = "admin";
        // Add the page number and owner to all pages
        writer.setPageEvent(getPageEvent(email, Arole));

        document.open();

        // Report type
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 15, Font.BOLDITALIC); // Title font size 2 sizes larger than body

        Paragraph title = new Paragraph("Admin Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Table with the user name and role from the database
        PdfPTable dataTable = new PdfPTable(3);
        dataTable.setWidthPercentage(100);

        // relative widths of the columns
        float[] columnWidths = {5f, 20f, 10f}; // Adjust the width percentage as needed
        dataTable.setWidths(columnWidths);

        PdfPCell cell = new PdfPCell(new Phrase("No."));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Username"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Role"));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        dataTable.addCell(cell);

        try {
            // Fetch data from the database
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT email, user_role FROM USER_INFO");

            int recordCount = 0;

            // Populate table with data (number increment, name, role)
            while (resultSet.next()) {
                String userName = resultSet.getString("email");
                String role = resultSet.getString("user_role");

                dataTable.addCell(String.valueOf(recordCount + 1));
                dataTable.addCell(userName);
                dataTable.addCell(role);

                recordCount++;

                // Check if the table exceeds the page height
                if (isTableExceedingPage(document, dataTable)) {
                    document.add(dataTable); // Add the current table to the document
                    document.newPage(); // Start a new page
                    document.add(title); // Add the title on each new page

                    // Create a new table for the next page
                    dataTable = new PdfPTable(3);
                    dataTable.setWidthPercentage(100);
                    dataTable.setWidths(columnWidths);

                    // Add table headers to the new table
                    cell = new PdfPCell(new Phrase("No."));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dataTable.addCell(cell);

                    cell = new PdfPCell(new Phrase("Username"));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dataTable.addCell(cell);

                    cell = new PdfPCell(new Phrase("Role"));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dataTable.addCell(cell);
                }
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add the last table to the document
        document.add(dataTable);

        document.close();

        // PDF to get displayed
        response.setContentType("application/pdf");
        response.setContentLength(baos.size());
        ServletOutputStream out = response.getOutputStream();
        baos.writeTo(out);
        out.flush();
    }

    private boolean isTableExceedingPage(Document document, PdfPTable table) {
        Rectangle pageSize = document.getPageSize();
        float pageHeight = pageSize.getHeight();
        float marginTop = document.topMargin();
        float marginBottom = document.bottomMargin();
        float currentHeight = pageHeight - marginTop - marginBottom;
        float tableHeight = table.getTotalHeight();
        return tableHeight > currentHeight;
    }

    private void generateGuestReport(HttpServletResponse response, String email, ServletConfig config) throws DocumentException, IOException {
        // customized size for the Guest report
        Rectangle customizedSize = new Rectangle(400, 400);
        Document document = new Document(customizedSize);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Report type
        Paragraph title = new Paragraph("Guest Report", FontFactory.getFont(FontFactory.HELVETICA, 15, Font.BOLDITALIC));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Fetch details of the guest user from the database based on the provided email
        String username = "";
        String decryptedPassword = "";

        try {
            // Connect to the database and fetch user details
            PreparedStatement statement = connection.prepareStatement("SELECT email, password FROM USER_INFO WHERE email = ?");
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            // If user found, retrieve username and decrypt password
            if (resultSet.next()) {
                username = resultSet.getString("email");
                String storedPassword = resultSet.getString("password").trim();
                try {
                    PasswordDecryption passwordDecryption = new PasswordDecryption(config);
                    // Decrypt the password
                    decryptedPassword = passwordDecryption.decrypt(storedPassword);
                } catch (Exception e) {
                    throw new RuntimeException("Error decrypting password", e);
                }
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add the details of the guest user to the PDF
        Paragraph userDetails = new Paragraph("\n\n\nUsername: " + username + "\nPassword: " + decryptedPassword);
        document.add(userDetails);

        // Add the page number and owner
        String role = "guest";
        writer.setPageEvent(getPageEvent(email, role));
        document.close();

        // PDF to get displayed
        response.setContentType("application/pdf");
        response.setContentLength(baos.size());
        ServletOutputStream out = response.getOutputStream();
        baos.writeTo(out);
        out.flush();
    }

    // Handles the footer elements
    private PdfPageEventHelper getPageEvent(String email, String role) {
        return new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                int totalNumberofPagesAdmin = 3;
                PdfContentByte cb = writer.getDirectContent();
                cb.beginText();

                // Set font size for page number and email to 2 sizes smaller than body
               
                Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC); // Footer font size 2 sizes smaller than body
                BaseFont footerBaseFont = footerFont.getBaseFont();
                cb.setFontAndSize(footerBaseFont, 10);

                // Display page number
                float pageNumberX = document.right() - 70;
                cb.setTextMatrix(pageNumberX, document.bottom() - 10);
                if(role.equals("admin")){
                    cb.showText("page " + writer.getPageNumber() + " of " + totalNumberofPagesAdmin);
                }
                else{
                    cb.showText("page " + writer.getPageNumber() + " of " + writer.getPageNumber());
                }

                // Display email
                cb.setTextMatrix(document.left(), document.bottom() - 10);
                cb.showText(email);

                cb.endText();
            }
        };
    }
    

    @Override
    public void destroy() {
        // Close the database connection when the servlet is destroyed
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
