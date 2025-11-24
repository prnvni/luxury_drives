import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/DeleteCarServlet")
public class DeleteCarServlet extends HttpServlet {


	private static final long serialVersionUID = 1L;
	private static final String URL = "jdbc:mysql://localhost:3306/luxury_drives";
    private static final String USER = "root";
    private static final String PASSWORD = "pranav123";

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            int id = Integer.parseInt(request.getParameter("id"));

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);

            PreparedStatement ps = con.prepareStatement("DELETE FROM cars WHERE id = ?");
            ps.setInt(1, id);
            
            int rows = ps.executeUpdate();

            ps.close();
            con.close();

            out.print("{\"success\":true}");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}