	import java.io.IOException;
	import java.io.PrintWriter;
	import java.sql.*;
	import java.util.ArrayList;
	import java.util.List;
	
	import jakarta.servlet.ServletException;
	import jakarta.servlet.annotation.WebServlet;
	import jakarta.servlet.http.HttpServlet;
	import jakarta.servlet.http.HttpServletRequest;
	import jakarta.servlet.http.HttpServletResponse;
	
	import com.fasterxml.jackson.databind.ObjectMapper;
	
	@WebServlet("/api/bookings")
	public class BookingListServlet extends HttpServlet {
	    private static final long serialVersionUID = 1L;
	
	    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	            throws ServletException, IOException {
	
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        
	        PrintWriter out = response.getWriter();
	        List<Booking> bookings = new ArrayList<>();
	        Connection conn = null;
	        PreparedStatement stmt = null;
	        ResultSet rs = null;
	
	        try {
	           
	            conn = DBConnection.getConnection();
	       
	            String sql = "SELECT * FROM bookings ORDER BY created_at DESC";
	            
	            stmt = conn.prepareStatement(sql);
	            rs = stmt.executeQuery(); 
	
	            while (rs.next()) {
	                Booking booking = new Booking();
	                booking.setId(rs.getInt("id"));
	                booking.setFullName(rs.getString("full_name"));
	                booking.setEmail(rs.getString("email"));
	                booking.setCarModel(rs.getString("car_model"));
	                booking.setStartDate(rs.getString("start_date"));
	                booking.setEndDate(rs.getString("end_date"));

	                booking.setStatus(rs.getString("status"));
	                booking.setPhoneNumber(rs.getString("phone_number"));
	                booking.setPickupLocation(rs.getString("pickup_location"));
	                booking.setAdditionalNotes(rs.getString("additional_notes"));
	                
	                bookings.add(booking);
	            }
	
	            ObjectMapper mapper = new ObjectMapper();
	            String jsonResponse = mapper.writeValueAsString(bookings);
	
	            out.print(jsonResponse);
	
	        } catch (SQLException | ClassNotFoundException e) {
	       
	            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            out.print("[]");
	            e.printStackTrace();
	
	        } finally {
	            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
	            if (stmt != null) try { stmt.close(); } catch (SQLException ignored) {}
	            DBConnection.closeConnection(conn);
	        }
	    }
	}