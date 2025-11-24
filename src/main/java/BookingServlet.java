


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet; 
import jakarta.servlet.http.HttpServlet;    
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/submitBooking")
public class BookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String name = request.getParameter("fullname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String car = request.getParameter("car_model");
        String start = request.getParameter("start_date");
        String end = request.getParameter("end_date");
        String location = request.getParameter("pickup_location");
        String notes = request.getParameter("notes");
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "INSERT INTO bookings (full_name, email, phone_number, car_model, start_date, end_date, pickup_location, additional_notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
          
            stmt = conn.prepareStatement(sql);
        
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, car);
          
            stmt.setString(5, start); 
            stmt.setString(6, end);
            stmt.setString(7, location);
            stmt.setString(8, notes);
          
            stmt.executeUpdate();
        
            response.sendRedirect(request.getContextPath() + "/html/home.html?booking=success");
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
         
            response.sendRedirect(request.getContextPath() + "/html/form.html?error=dbfail");
            
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException ignore) {}
            DBConnection.closeConnection(conn);
        }
    }
}