import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/AddCarServlet")
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024,      // file kept in memory until it reaches 1 mb
	    maxFileSize = 1024 * 1024 * 5,        // maximum size of one uploaded file is 5 mb
	    maxRequestSize = 1024 * 1024 * 10     // total size of the whole request is limited to 10 mb
)

public class AddCarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String UPLOAD_DIR = "uploads"; // folder for storing images

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Connection con = null;

        try {
            // load driver and open db connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DBConnection.getConnection();
            
            // reading all normal form fields
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
            
            // handle file upload and build image path
            String imagePath = "";
            Part filePart = request.getPart("image");

            if (filePart != null && filePart.getSize() > 0) {

                // get original file name
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

                // real folder path inside server
                String uploadPath = getServletContext().getRealPath("/" + UPLOAD_DIR);

                // create folder if missing
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
                
                // save uploaded image
                filePart.write(uploadPath + File.separator + fileName);

                // relative path stored in db
                imagePath = request.getContextPath() + "/" + UPLOAD_DIR + "/" + fileName;
            }


            // insert into main cars table
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

            // insert into child table depending on category
            if (category.equals("Luxury")) {
                // luxury car gets mileage
                PreparedStatement psLux = con.prepareStatement(
                    "INSERT INTO luxury_cars (car_id, mileage) VALUES (?, ?)"
                );
                psLux.setInt(1, id);
                psLux.setDouble(2, Double.parseDouble(mileage));
                psLux.executeUpdate();
                psLux.close();
            }
            else if (category.equals("Sports")) {
                // sport car gets top speed
                PreparedStatement psSport = con.prepareStatement(
                    "INSERT INTO sport_cars (car_id, top_speed) VALUES (?, ?)"
                );
                psSport.setInt(1, id);
                psSport.setInt(2, Integer.parseInt(topSpeed));
                psSport.executeUpdate();
                psSport.close();
            }
            else if (category.equals("SUV")) {
                // suv gets seating capacity
                PreparedStatement psSuv = con.prepareStatement(
                    "INSERT INTO suv_cars (car_id, seating_capacity) VALUES (?, ?)"
                );
                psSuv.setInt(1, id);
                psSuv.setInt(2, Integer.parseInt(seating));
                psSuv.executeUpdate();
                psSuv.close();
            }
            else if (category.equals("Sedan")) {
                // sedan gets trunk space
                PreparedStatement psSedan = con.prepareStatement(
                    "INSERT INTO sedan_cars (car_id, trunk_space_liters) VALUES (?, ?)"
                );
                psSedan.setInt(1, id);
                psSedan.setInt(2, Integer.parseInt(trunkSpace));
                psSedan.executeUpdate();
                psSedan.close();
            }

            // all inserts done
            con.close();
            out.print("{\"success\":true}");

        } catch (Exception e) {
            // send error response if anything breaks
            e.printStackTrace();
            out.print("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
