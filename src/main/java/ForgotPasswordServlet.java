import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String action = request.getParameter("action");
        String email = request.getParameter("email");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(); 

            if ("check".equals(action)) {
                //checking if email exists in either users or admins table
                boolean found = false;
                
                //checking users
                stmt = conn.prepareStatement("SELECT id FROM users WHERE email = ?");
                stmt.setString(1, email);
                rs = stmt.executeQuery();
                if (rs.next()) found = true;
                
                //if not found in users,check admins
                if (!found) {
                    stmt.close();
                    stmt = conn.prepareStatement("SELECT id FROM admins WHERE email = ?");
                    stmt.setString(1, email);
                    rs = stmt.executeQuery();
                    if (rs.next()) found = true;
                }

                if (found) {
                    out.print("{\"status\":\"success\"}");
                } else {
                    out.print("{\"status\":\"failure\", \"message\":\"Email not found in our records.\"}");
                }

            } else if ("update".equals(action)) {
                //updating password
                String newPass = request.getParameter("password");
                
                //try updating in users table first
                stmt = conn.prepareStatement("UPDATE users SET password = ? WHERE email = ?");
                stmt.setString(1, newPass);
                stmt.setString(2, email);
                int rows = stmt.executeUpdate();

                //if no rows updated in users, try admins
                if (rows == 0) {
                    stmt.close();
                    stmt = conn.prepareStatement("UPDATE admins SET password = ? WHERE email = ?");
                    stmt.setString(1, newPass);
                    stmt.setString(2, email);
                    rows = stmt.executeUpdate();
                }

                if (rows > 0) {
                    out.print("{\"status\":\"success\"}");
                } else {
                    out.print("{\"status\":\"failure\", \"message\":\"Could not update password.\"}");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}