import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/updateScore")
public class SubmitScoreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Set the response type
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Retrieve the score parameter from the request
            String scoreParam = request.getParameter("score");
            if (scoreParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Set 400 Bad Request status
                out.println("{\"status\":\"failure\", \"message\":\"Score parameter is missing.\"}");
                return;
            }

            int score;
            try {
                score = Integer.parseInt(scoreParam); // Convert score to int
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Set 400 Bad Request status
                out.println("{\"status\":\"failure\", \"message\":\"Invalid score format.\"}");
                return;
            }

            // Get the current logged-in username
            HttpSession session = request.getSession(false);
            String username = (session != null) ? (String) session.getAttribute("username") : null;

            if (username != null) {
                // Database connection
                String url = "jdbc:mysql://localhost:3306/realdb"; // Replace with your database URL
                String user = "root"; // Replace with your database user
                String password = "krishna@30"; // Replace with your database password

                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    // Update the user's score in the users table
                    String sql = "UPDATE users SET score = ? WHERE username = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setInt(1, score);
                        preparedStatement.setString(2, username);

                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            out.println("{\"status\":\"success\"}");
                        } else {
                            out.println("{\"status\":\"failure\", \"message\":\"No rows affected for username: " + username + "\"}");
                        }
                    }
                }
            } else {
                out.println("{\"status\":\"failure\", \"message\":\"User not logged in.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Set 500 Internal Server Error status
            out.println("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }
}
