import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/AddCarServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 1024 * 1024 * 5,         // 5 MB
    maxRequestSize = 1024 * 1024 * 10      // 10 MB
)
public class AddCarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
	private static final String URL = "jdbc:mysql://localhost:3306/luxury_drives";
    private static final String USER = "root";
    private static final String PASSWORD = "pranav123";
    
    // Change this to your actual upload directory
    private static final String UPLOAD_DIR = "uploads";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            
            // Get form parameters
            int id = Integer.parseInt(request.getParameter("id"));
            String brand = request.getParameter("brand");
            String model = request.getParameter("model");
            int year = Integer.parseInt(request.getParameter("year"));
            String category = request.getParameter("category");
            String mileage = request.getParameter("mileage");
            String topSpeed = request.getParameter("top_speed");
            String trunkSpace = request.getParameter("trunk_space_liters");
            String seating = request.getParameter("seating_capacity");
            double price = Double.parseDouble(request.getParameter("price"));
            String engine = request.getParameter("engine");
            String transmission = request.getParameter("transmission");
            String drivetrain = request.getParameter("drivetrain");
            String fuelType = request.getParameter("fuelType");
            String color = request.getParameter("color");
            String interior = request.getParameter("interior");
            String features = request.getParameter("features");
            
            // Handle file upload
            String imagePath = "";
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

				String uploadPath = getServletContext().getRealPath("/" + UPLOAD_DIR);

                
                // Create upload directory if it doesn't exist
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
                
                // Save the file
                filePart.write(uploadPath + File.separator + fileName);
                imagePath = request.getContextPath() + "/" + UPLOAD_DIR + "/" + fileName;
            }

            // Fixed: 15 columns need 15 placeholders
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO cars (id, brand, model, year, category, price, engine, transmission, drivetrain, fuel_type, color, interior, features, image) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
            );

            ps.setInt(1, id);
            ps.setString(2, brand);
            ps.setString(3, model);
            ps.setInt(4, year);
            ps.setString(5, category);
            ps.setDouble(6, price);
            ps.setString(7, engine);
            ps.setString(8, transmission);
            ps.setString(9, drivetrain);
            ps.setString(10, fuelType);
            ps.setString(11, color);
            ps.setString(12, interior);
            ps.setString(13, features);
            ps.setString(14, imagePath);

            ps.executeUpdate();
            ps.close();
            

            
         // After inserting into cars table, add this:

         // Insert into category-specific table
         if (category.equals("Luxury")) {
             PreparedStatement psLux = con.prepareStatement(
                 "INSERT INTO luxury_cars (car_id, mileage) VALUES (?, ?)"
             );
             psLux.setInt(1, id);
             psLux.setDouble(2, Double.parseDouble(mileage)); // Default mileage or get from form
             psLux.executeUpdate();
             psLux.close();
         }
         else if (category.equals("Sports")) {
             PreparedStatement psSport = con.prepareStatement(
                 "INSERT INTO sport_cars (car_id, top_speed) VALUES (?, ?)"
             );
             psSport.setInt(1, id);
             psSport.setInt(2, Integer.parseInt(topSpeed)); // Default or get from form
             psSport.executeUpdate();
             psSport.close();
         }
         else if (category.equals("SUV")) {
             PreparedStatement psSuv = con.prepareStatement(
                 "INSERT INTO suv_cars (car_id, seating_capacity) VALUES (?, ?)"
             );
             psSuv.setInt(1, id);
             psSuv.setInt(2, Integer.parseInt(seating)); // Default or get from form
             psSuv.executeUpdate();
             psSuv.close();
         }
         else if (category.equals("Sedan")) {
             PreparedStatement psSedan = con.prepareStatement(
                 "INSERT INTO sedan_cars (car_id, trunk_space_liters) VALUES (?, ?)"
             );
             psSedan.setInt(1, id);
             psSedan.setInt(2, Integer.parseInt(trunkSpace)); // Default or get from form
             psSedan.executeUpdate();
             psSedan.close();
         }
         con.close();
         out.print("{\"success\":true}");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}