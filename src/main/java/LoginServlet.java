import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database Credentials (match your DBConnection class)
    private static final String URL = "jdbc:mysql://localhost:3306/carrental_db"; 
    private static final String USER = "root";
    private static final String PASSWORD = "Aikarakundeez123"; // Verify your password

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String loginType = request.getParameter("loginType"); //"user" or "admin"

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            String sql;
            if ("admin".equals(loginType)) {
                //checking admin table
                sql = "SELECT id, full_name, email FROM admins WHERE email = ? AND password = ?";
            } else {
                //checking user table
                sql = "SELECT id, full_name, email FROM users WHERE email = ? AND password = ?";
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password); 

            rs = stmt.executeQuery();

            if (rs.next()) {
                //LOGIN SUCCESS
                int id = rs.getInt("id");
                String name = rs.getString("full_name");

                //create session
                HttpSession session = request.getSession();
                session.setAttribute("userId", id);
                session.setAttribute("userRole", loginType);
                session.setAttribute("userName", name);

                //return success JSON
                out.print("{\"status\":\"success\", \"role\":\"" + loginType + "\", \"name\":\"" + name + "\"}");
            } else {
                //LOGIN FAILED
                out.print("{\"status\":\"failure\", \"message\":\"Invalid email or password.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"Database error: " + e.getMessage() + "\"}");
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }
}