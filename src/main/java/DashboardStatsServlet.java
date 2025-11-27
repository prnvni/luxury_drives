import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper; //Jackson library


//BOOKINGS BY CAR CATEGORY PIE CHART


@WebServlet("/DashboardStatsServlet")
public class DashboardStatsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        //lists to hold data
        List<String> labels = new ArrayList<>();
        List<Integer> dataPoints = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            
            //SQL Query: joining bookings with cars to count bookings per category
           
            String sql = "SELECT c.category, COUNT(b.id) AS count " +
                    "FROM bookings b " +
                    "JOIN cars c ON b.car_model LIKE CONCAT('%', c.model, '%') " +
                    "GROUP BY c.category";

            
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while(rs.next()) {
                labels.add(rs.getString("category"));
                dataPoints.add(rs.getInt("count"));
            }
            
            //creating a simple map to send as JSON
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("labels", labels);
            chartData.put("data", dataPoints);
            
            //using Jackson to convert Map to JSON string
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(chartData);
            
            out.print(json);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}