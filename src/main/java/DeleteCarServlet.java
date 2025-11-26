import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/DeleteCarServlet")
public class DeleteCarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Connection con = null;
        
        try {
            // read id of car that needs to be deleted
            int id = Integer.parseInt(request.getParameter("id"));

            // connect to database
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DBConnection.getConnection();

            // delete car from main table
            PreparedStatement ps = con.prepareStatement("DELETE FROM cars WHERE id = ?");
            ps.setInt(1, id);
            
            int rows = ps.executeUpdate(); // number of deleted rows

            ps.close();
            con.close();

            // send success response to frontend
            out.print("{\"success\":true}");

        } catch (Exception e) {
            // send error message if something goes wrong
            e.printStackTrace();
            out.print("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
