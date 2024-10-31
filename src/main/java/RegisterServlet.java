import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email"); // New email field

        // Database credentials
        String jdbcURL = "jdbc:mysql://localhost:3306/realdb?useSSL=false&allowPublicKeyRetrieval=true";
        String dbUser = "root";
        String dbPassword = "krishna@30";

        // SQL query to insert a new user, including the email
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                // Set parameters
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, email); // Set email

                // Execute update to insert the user
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    response.sendRedirect("login.html"); // Redirect to login page if registration is successful
                } else {
                    response.getWriter().println("Registration failed, please try again.");
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("Error: JDBC Driver not found. " + e.getMessage());
            throw new ServletException("Registration failed due to internal error.");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage()); // For debugging only
            throw new ServletException("Registration failed due to internal error.");
        }
    }
}
